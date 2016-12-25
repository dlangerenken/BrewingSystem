/*
 * 
 */
package impl;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.ActuatorDetails;
import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingProcessSummary.TemperatureLevelInfo;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HardwareStatus;
import general.IngredientAddition;
import general.Recipe;
import interfaces.IAcousticNotifier;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IStirrerService;
import interfaces.ITemperatureLogger;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import messages.BrewingAbortedMessage;
import messages.BrewingCompleteMessage;
import messages.BrewingStartMessage;
import messages.ConfirmationMessage;
import messages.ConfirmationRequestMessage;
import messages.EndMessage;
import messages.Message;
import messages.PreNotificationMessage;
import messages.StartMessage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import persistence.PersistenceHandler;
import utilities.CollectionUtil;
import utilities.PropertyUtil;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * The BrewingController which handles every interaction inside of a brewing process
 * 
 * @author Max and Patrick
 *
 */
@Singleton
public class BrewingController implements IBrewingController {
  /** The logger which saves all the information into log files */
  private static final Logger LOGGER = LogManager.getLogger();

  /** currentBrewingProcess is null when no recipe should be in brewing state. */
  private BrewingProcess currentBrewingProcess;

  /** The hop cooker brewing part. */
  private final BrewingPart hopCooker;

  /** The log service. */
  private final IBrewingLogService logService;

  /** The masher brewing part. */
  private final BrewingPart masher;

  /** Map of brewing-state and Handler-implementations. */
  private Map<BrewingState.State, BrewingStateInterface> stateMap;

  /**
   * PushMessages which are stored for the webclient during a single brewing process
   */
  private List<Message> pushedMessages;

  /** The stirrer service. */
  private final IStirrerService stirrerService;

  /** The temperature service. */
  private final ITemperatureService temperatureService;

  /** The logger that gets notified at every temperature change. **/
  private final ITemperatureLogger temperatureLogger;

  /** The current summary */
  private BrewingProcessSummary brewingProcessSummary;

  /** The acoustic notifier that is called during notifications */
  private final IAcousticNotifier acousticNotifier; // os220215

  /**
   * The thread for checking whether a request has not received a response in the given timeout
   * interval
   */
  private ResposeWaitingThread responseWaitingTread;

  /**
   * UserFacade as Provider to avoid cycles within the dependency injection
   */
  private final Provider<IUserFacadeService> userFacade;

  /** persistence handler */
  private final PersistenceHandler persistenceHandler;


  /**
   * Instantiates a new brewing controller.
   *
   * @param temperatureService the temperature service
   * @param recipeService the recipe service
   * @param brewingLogService the brewing log service
   * @param stirrerService the stirrer service
   * @param userFacadeService the user facade service
   * @param masher the masher
   * @param hopCooker the hop cooker
   */
  @Inject
  public BrewingController(final ITemperatureService temperatureService,
      final IBrewingLogService brewingLogService, final IStirrerService stirrerService,
      final @Named("Masher") BrewingPart masher, final @Named("HopCooker") BrewingPart hopCooker,
      final ITemperatureLogger temperatureLogger, final IAcousticNotifier acousticNotifier,
      final Provider<IUserFacadeService> userFacadeProvider,
      final PersistenceHandler persistenceHandler) {
    this.persistenceHandler = persistenceHandler;
    this.temperatureLogger = temperatureLogger;
    this.temperatureService = temperatureService;
    this.logService = brewingLogService;
    this.stirrerService = stirrerService;
    this.masher = masher;
    this.hopCooker = hopCooker;
    this.acousticNotifier = acousticNotifier; // os220215
    this.userFacade = userFacadeProvider;
    init();
    LOGGER.info("BrewingController constructed");
  }

  /**
   * Creates a new ResponseWaiting Thread, BrewingProcessSummary, initiated the subsriber set and
   * the state maps.
   */
  private void init() {
    this.responseWaitingTread = new ResposeWaitingThread();
    brewingProcessSummary = new BrewingProcessSummary(null);
    pushedMessages = new ArrayList<>();
    initStateMap();
    addShutdownHook();
  }

  /**
   * Shutsdown the brewing-controller if System.exit(0) is called or the jvm crashed
   */
  private void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (stirrerService != null) {
          try {
            stirrerService.stopStirring();
          } catch (Exception e) {
            /*
             * Ignore as the shutdown hook is the last operation done
             */
          }
        }
        if (temperatureService != null) {
          try {
            temperatureService.stop();
          } catch (Exception e) {
            /*
             * Ignore as the shutdown hook is the last operation done
             */
          }
        }
        if (acousticNotifier != null) {
          acousticNotifier.switchOff();
        }
        try {
          terminate(false);
        } catch (Exception e) {
          /*
           * ignore this here when shutdown-hook is called
           */
        }
      }
    });
  }

  /**
   * Initializes the state-map with corresponding handlers
   */
  private void initStateMap() {
    stateMap = new HashMap<State, BrewingStateInterface>();
    stateMap.put(State.NOT_STARTED, new NotStartedStateHandler());
    stateMap.put(State.HOP_COOKING, new HopCookingStateHandler());
    stateMap.put(State.LAUTERING, new LauteringStateHandler());
    stateMap.put(State.MASHING, new MashingStateHandler());
    stateMap.put(State.WHIRLPOOL, new WhirlpoolStateHandler());
    stateMap.put(State.FINISHED, new FinishedStateHandler());
  }

  /**
   * Ends the current brewing process, finishes the log, interrupts the response waiting thread and
   * unsubscribes the temperature logger
   * 
   * @param success whether the process was ended regulary or not
   * @throws BrewingProcessNotFoundException no brewing process ongoing
   */
  private void exitBrewingProcess(final boolean success) throws BrewingProcessNotFoundException {
    LOGGER
        .info("The brewing process has exited " + (success ? "successfully." : "unsuccessfully."));
    responseWaitingTread.interrupt();
    temperatureLogger.unsubscribe();
    if (success || currentBrewingProcess == null) {
      resetBrewingRessources();
    } else {
      /* ask the user to confirm the canceled state */
      notify(new ConfirmationRequestMessage(currentBrewingProcess.getState()));
    }
  }

  /**
   * Frees the references to any resources used during brewing (e.g. BrewingSummary) and initializes
   * the brewing controller for a new brewing process.
   */
  private void resetBrewingRessources() {
    try {
      masher.finish();
      hopCooker.finish();
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.error("Could not finish brewing process.", e);
    }
    if (responseWaitingTread != null && responseWaitingTread.isAlive()) {
      responseWaitingTread.interrupt();
      responseWaitingTread = null;
    }
    try {
      logService.finishLog();
    } catch (BrewingProcessNotFoundException e) {
      /*
       * ignore as otherwise the interrupt and other calls are not executed
       */
    }
    currentBrewingProcess = null;
    init();
  }

  /**
   * Changes the type of the current brewing state to NORMAL
   */
  private void changeStateToNormal() {
    if (currentBrewingProcess != null && currentBrewingProcess.getState() != null) {
      currentBrewingProcess.changeState(Type.NORMAL);
    }
  }

  /**
   * Handles the confirmation of a specified BrewingState
   * 
   * @param confirmedState the BrewingState that is to be confirmed
   */
  @Override
  public void confirmStep(final BrewingState confirmedState)
      throws BrewingProcessNotFoundException, InvalidBrewingStepException {
    /* if there is no brewing process we need to stop right here */
    if (currentBrewingProcess == null) {
      throw new BrewingProcessNotFoundException("No brewing process ongoing");
    }

    BrewingState currentState = currentBrewingProcess.getState();

    /* if the current brewing process was canceled, set it to null */
    if (Type.CANCEL.equals(currentBrewingProcess.getState().getType())) {
      if (confirmedState.getPosition() == currentState.getPosition()
          && confirmedState.getState() == currentState.getState()) {
        try {
          exitBrewingProcess(true);
          return;
        } catch (BrewingProcessNotFoundException e) {
          /*
           * Do nothing here, because if there is an exception then because the brewing process
           * already exited
           */
          resetBrewingRessources();
          return;
        }
      }
    }

    /* logs the confirmation state */
    logService.log(new ConfirmationMessage(currentState, confirmedState));

    /* check whether the current brewing state is in request state */
    if (Type.NORMAL.equals(currentBrewingProcess.getState().getType())) {
      throw new InvalidBrewingStepException("There is no request to be confirmed.");
    }

    /*
     * if the current state is not equivalent to the confirmed-state there has to be an exception
     * (as we cannot confirm previous states or states which aren't started yet
     */
    if (currentState.toValue() != confirmedState.toValue()) {
      throw new InvalidBrewingStepException(String.format(
          "The confirmed state is not equal to the requested state. Requested: %d, Confirmed: %d",
          currentState.toValue(), confirmedState.toValue()));
    }

    /*
     * if we dont have a valid state we should stop here as well
     */
    if (stateMap.containsKey(confirmedState.getState())) {
      BrewingStateInterface currentStateInterface = stateMap.get(confirmedState.getState());
      currentStateInterface.confirmState(confirmedState);
    } else {
      throw new InvalidBrewingStepException("The BrewingState.State " + confirmedState.getState()
          + " should not exist.");
    }
  }

  @Override
  public ActuatorDetails getCurrentActuatorDetails() {
    ActuatorDetails details = new ActuatorDetails();
    details.setStirrerStatus(stirrerService.getStatus());
    details.setHeaterStatus(temperatureService.getHeaterStatus());
    details.setTemperatureSensorStatus(temperatureService.getTemperatureSensorStatus());
    if (details.getTemperatureSensorStatus() == HardwareStatus.ENABLED) {
      details.setTemperature(temperatureService.getTemperature());
    }
    return details;
  }


  @Override
  public BrewingProcess getCurrentBrewingProcess() {
    return currentBrewingProcess;
  }

  /**
   * Notify differs between confirmation request messages and other ones, because the confirmation
   * request messages are logged and trigger the confirmation waiting thread to listen for a
   * response.
   */
  @Override
  public void notify(final Message message) {
    if (message instanceof PreNotificationMessage) {
      this.acousticNotifier.sendPreNotificationBeep(); // os220215
      pushedMessages.add(message);
    }
    if (message instanceof ConfirmationRequestMessage) {
      this.acousticNotifier.sendConfirmationRequestBeep(); // os220215
      sendConfirmationRequest((ConfirmationRequestMessage) message);
    } else {
      if (userFacade != null) {
        userFacade.get().notify(message);
      } else {
        // throw new BrewingProcessException();
      }
    }
  }

  /**
   * Sends a confirmation request message using the current brewing state to the client and logs
   * this message.
   */
  private void sendConfirmationRequest() {
    sendConfirmationRequest(new ConfirmationRequestMessage(currentBrewingProcess.getState()));
  }

  /**
   * Sends a confirmation request message using to the client and logs it.
   */
  private void sendConfirmationRequest(final ConfirmationRequestMessage confirmationRequestMessage) {
    if (!isManualStepRequest(confirmationRequestMessage.getBrewingStep())) {
      if (confirmationRequestMessage.getBrewingStep() != null) {
        Collection<IngredientAddition> requests =
            CollectionUtil.getTypedCollectionFromObject(confirmationRequestMessage.getBrewingStep()
                .getData(), IngredientAddition.class);
        if (responseWaitingTread != null && responseWaitingTread.isAlive()) {
          responseWaitingTread.requestSent(requests);
        } else {
          LOGGER.warn("ResponseWaitingThread is null");
        }
      }
    }
    userFacade.get().notify(confirmationRequestMessage);
    try {
      logService.log(confirmationRequestMessage);
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.warn("Could not log the request " + confirmationRequestMessage + ".", e);
    }
  }

  /**
   * Returns true if the current brewing state is a manual step.
   * 
   * @param state
   * @return
   */
  private boolean isManualStepRequest(final BrewingState state) {
    return state.getState() == State.LAUTERING || state.getState() == State.WHIRLPOOL;
  }

  /**
   * Notifies the user via UserFacade that the mashing process has been initialized and the
   * controller is waiting for his response to start.
   */
  private void sendMashingStartRequest() {
    currentBrewingProcess.setState(Type.REQUEST, State.MASHING, Position.START);
    sendConfirmationRequest();
  }

  /**
   * Sets the brewing service into a state after getting the concurrent confirmation.
   * 
   * @param confirmedPosition
   * @throws BrewingProcessNotFoundException
   */
  @Override
  public void setConfirmedState(final Position confirmedPosition)
      throws BrewingProcessNotFoundException {
    if (currentBrewingProcess == null) {
      throw new BrewingProcessNotFoundException("No brewing process ongoing");
    }
    currentBrewingProcess.changeState(Type.NORMAL);
    currentBrewingProcess.changeState(confirmedPosition);
    currentBrewingProcess.getState().setData(null);
  }

  /**
   * Sets the brewing controller into a state waiting for a request-confirmation. This can be
   * executed by Masher or HopCooking, leaving the BrewingState.State untouched, setting the
   * BrewingState.Type to REQUESTED and BrewingState.Position to requestedPosition. Example: Masher
   * executes setRequestState(BrewingState.Position.IODINE) to allow the client to confirm the
   * iodine test.
   * 
   * @param requestedPosition
   * @throws BrewingProcessNotFoundException if there is no running brewing process
   */
  @Override
  public void setRequestState(final BrewingState.Position requestedPosition,
      final BrewingState.Type requestedType, final Object data)
      throws BrewingProcessNotFoundException {
    if (currentBrewingProcess != null) {
      currentBrewingProcess.changeState(requestedType);
      currentBrewingProcess.changeState(requestedPosition);
      currentBrewingProcess.getState().setData(data);
    } else {
      throw new BrewingProcessNotFoundException("No brewing process ongoing");
    }
  }

  /**
   * Starts the brewing process with a given recipe and notifies the user, whether the starting was
   * successful or this controller is already occupied with another brewing.
   *
   * @param r is the recipe to be used
   * @throws BrewingProcessException in case of: there is already an ongoing brewing process the
   *         recipe is null the mashing or hop cooking fails
   */
  @Override
  public void startBrewing(final Recipe recipe) throws BrewingProcessException {
    if (currentBrewingProcess != null) {
      throw new BrewingProcessException("Another brewing process is already running: "
          + currentBrewingProcess.getRecipe().getId() + " - "
          + currentBrewingProcess.getRecipe().getName());
    } else if (recipe == null) {
      throw new BrewingProcessException("Can not start brewing process with recipe \"null\".");
    } else {
      String recipeError = recipe.getErrorMessage();
      if (recipeError != null) {
        throw new BrewingProcessException("The current recipe is invalid: " + recipe.getId()
            + " - " + recipe.getName() + ". Message: " + recipeError);
      } else {
        /* everything is alright, start brewing process */
        currentBrewingProcess = new BrewingProcess(recipe, persistenceHandler.getLowestUnusedId());
        currentBrewingProcess.start();
        brewingProcessSummary.setRecipeId(recipe.getId());
        brewingProcessSummary.clearTemperatureLevelInfo();
        logService.startLog(currentBrewingProcess);
        logService.log(new BrewingStartMessage(currentBrewingProcess.getRecipe().getName()));
        // os200215: Move this Method-Call to MashingStateHandler, HopCookingStateHandler.
        // temperatureLogger.subscribeToTemperatureController(temperatureService);
        responseWaitingTread.start();
        /* tell the user the current state and ask for start confirmation */
        sendMashingStartRequest();
      }
    }
  }

  /**
   * Terminates the brewing process after an user-request.
   *
   * @param userCaused whether the termination was initialized by the user or the brewing controller
   *        itself
   * @throws BrewingProcessNotFoundException
   */
  void terminate(final boolean userCaused) throws BrewingProcessNotFoundException {
    BrewingAbortedMessage message =
        new BrewingAbortedMessage(userCaused ? "Cancelled" : "RequestResponseTimeout");
    try {
      logService.log(message);
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.warn("Could not log BrewingAbortedMessage "
          + message + ".", e);
    }
    BrewingProcessNotFoundException thrownException = null;
    thrownException = finishMashing();
    thrownException = finishHopCooking();

    if (currentBrewingProcess != null) {
      currentBrewingProcess.changeState(Type.CANCEL);
    } else {
      thrownException = new BrewingProcessNotFoundException("CurrentBrewingProcess is null");
    }
    userFacade
        .get()
        .alarm(
            "Brewing process was aborted. Timeout for current confirmation request was not confirmed in time");
    exitBrewingProcess(false);
    if (thrownException != null) {
      throw thrownException;
    }
  }

  /**
   * Tries to finish the mashing process and returns an exception, if it failed.
   * 
   * @return
   */
  private BrewingProcessNotFoundException finishMashing() {
    try {
      if (masher != null) {
        masher.finish();
      }
      return null;
    } catch (BrewingProcessNotFoundException e) {
      return e;
    }
  }

  /**
   * Tries to finish the hop cooking process and returns an exception, if it failed.
   * 
   * @return
   */
  private BrewingProcessNotFoundException finishHopCooking() {
    try {
      if (hopCooker != null) {
        hopCooker.finish();
      }
      return null;
    } catch (BrewingProcessNotFoundException e) {
      return e;
    }
  }

  /**
   * Changes the maximalWaitingTimeMillis for the responsWaitingThread
   * 
   * @param maximalWaitingTimeMillis
   */
  public void setMaximalWaitingTimeMillis(final long maximalWaitingTimeMillis) {
    if (responseWaitingTread != null) {
      responseWaitingTread.setMaximalWaitingTimeMillis(maximalWaitingTimeMillis);
    }
  }

  @Override
  public void cancelCurrentBrewingProcess() throws BrewingProcessNotFoundException {
    terminate(true);
  }

  @Override
  public BrewingProcessSummary getCurrentBrewingProcessSummary()
      throws BrewingProcessNotFoundException {
    if (currentBrewingProcess != null && currentBrewingProcess.getState() != null) {
      brewingProcessSummary.setBrewingState(currentBrewingProcess.getState());


      return brewingProcessSummary;
    }
    throw new BrewingProcessNotFoundException("No brewing process ongoing");
  }

  /* Interfaces */
  /**
   * Interface for dealing with brewing-states
   */
  private interface BrewingStateInterface {

    /**
     * This method confirms the corresponding state
     * 
     * @param state State which should be confirmed
     * @throws InvalidBrewingStepException thrown if the current state was not expected
     */
    void confirmState(final BrewingState state) throws InvalidBrewingStepException;
  }

  /**
   * This class manages the time between a request is sent to the user and its response arrives and
   * terminates the brewing process if this exceeds a maximum threshold.
   * 
   * @author Patrick
   *
   */
  private final class ResposeWaitingThread extends Thread {
    /* time to sleep between checking of extended waiting time */
    private final long sleepingIntervalMillis = 100;

    /* maximum amount of time a request can remain unanswered */
    private long maximalWaitingTimeMillis;
    /* stores the request sent times for the different ingredients */
    private final Map<IngredientAddition, Long> ingredientAdditionRequestSentAt;
    private boolean brewing;

    /**
     * creates a new ResposeWaitingThread and gets the maximum waiting time from the properties file
     */
    public ResposeWaitingThread() {
      maximalWaitingTimeMillis =
          PropertyUtil
              .getPropertyLong(PropertyUtil.MILLIS_TO_REQUEST_CONFIRMATION_TIMEOUT_PROPERTY);
      brewing = true;
      ingredientAdditionRequestSentAt = new HashMap<IngredientAddition, Long>();
    }

    /**
     * Sets the maximumg waiting time
     * 
     * @param maximalWaitingTimeMillis
     */
    void setMaximalWaitingTimeMillis(final long maximalWaitingTimeMillis) {
      this.maximalWaitingTimeMillis = maximalWaitingTimeMillis;
    }

    /**
     * Adds a list of requests to the monitored ones.
     * 
     * @param requests
     */
    void requestSent(final Collection<IngredientAddition> requests) {
      if (requests != null) {
        Long time = Long.valueOf(System.currentTimeMillis());
        for (IngredientAddition request : requests) {
          LOGGER.info("Request sent: " + request);
          if (!ingredientAdditionRequestSentAt.containsKey(request)) {
            ingredientAdditionRequestSentAt.put(request, time);
          }
        }
      }
    }

    /**
     * Removes a list of requests after they have been confirmed.
     * 
     * @param responses
     */
    void responseReceived(final Collection<IngredientAddition> responses) {
      if (responses != null) {
        for (IngredientAddition response : responses) {
          LOGGER.info("Response received: " + response);
          ingredientAdditionRequestSentAt.remove(response);
        }
      }
    }

    /**
     * Checks if there is one request that exists for more than the maximum amount of time allowed
     * and terminates the brewing if so.
     */
    void checkTimeout() {
      /*
       * if no ingredient addition request is sent, we can skip the timeout check
       */
      if (ingredientAdditionRequestSentAt.isEmpty()) {
        return;
      }
      /*
       * check for every ingredient addition with response pending if it has reached the maximum
       * timeout
       */
      for (Iterator<Entry<IngredientAddition, Long>> iterator =
          ingredientAdditionRequestSentAt.entrySet().iterator(); iterator.hasNext();) {
        Entry<IngredientAddition, Long> ingredient = iterator.next();
        long timePassed = System.currentTimeMillis() - ingredient.getValue().longValue();
        /*
         * If the maximal waiting period is not reached yet, skip this element and continue with the
         * next ingredient
         */
        if (timePassed < maximalWaitingTimeMillis) {
          continue;
        }
        brewing = false;
        LOGGER
            .log(Level.WARN,
                "The time for a request confirmation has run out. The brewing process will be canceled.");
        try {
          terminate(false);
        } catch (BrewingProcessNotFoundException e) {
          LOGGER.warn("Could not log terminate the brewing process.", e);
        }
        break;
      }
    }

    @Override
    public void run() {
      while (brewing) {
        try {
          Thread.sleep(sleepingIntervalMillis);
        } catch (InterruptedException e) {
          LOGGER.warn("ResponseWaitingThread could not sleep.", e);
        }
        checkTimeout();
      }
    }
  }

  /**
   * This class deals with all actions within the finished state
   */
  private final class FinishedStateHandler implements BrewingStateInterface {
    @Override
    public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
      /*
       * making sure the process is in the right state
       */
      if (Position.END.equals(state.getPosition())) {
        endBrewing();
      } else {
        throw new InvalidBrewingStepException("Can not terminate the brewing process in "
            + state.getState() + ".");
      }
    }

    /**
     * Terminates the brewing process without further checks. Logs a brewing complete message and
     * sets the currentBrewingProcess to null
     */
    private void endBrewing() {
      try {
        logService.log(new BrewingCompleteMessage(currentBrewingProcess.getRecipe().getName()));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.info("The brewing process is already finished.", e);
      }
      try {
        exitBrewingProcess(true);
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.error("Brewing Process could not be ended as no process was found", e);
      }
    }
  }

  /**
   * This class deals with all actions within the hop-cooking state
   */
  private final class HopCookingStateHandler implements BrewingStateInterface {
    @Override
    public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
      /*
       * HopCooking only gets a confirmation by the user at the start HopCooking then sends a
       * notification when the process ended, but the user does not need to confirm the end
       */
      switch (state.getPosition()) {
        case START:
          changeStateToNormal();

          LOGGER.info("Confirmation for HOP_COOKING start received");
          try {
            // Temperature during Hop Cooking needs to be logged, os200215
            temperatureLogger.subscribeToTemperatureController(temperatureService);

            /* cut off all mashing temperature level information */
            brewingProcessSummary.clearTemperatureLevelInfo();

            hopCooker.startAction(currentBrewingProcess.getRecipe().getHopCookingPlan());
            brewingProcessSummary.setStartTimeHopCooking(System.currentTimeMillis());
            logService.log(new StartMessage(State.HOP_COOKING));
          } catch (BrewingProcessException e) {
            throw new InvalidBrewingStepException(
                "Can not start hop cooking right now. Coming from BrewinProcessException: "
                    + e.getMessage());
          }
          break;
        case ADDING:
          hopCooker.confirmState(state);
          /*
           * these statements are only reached of no exception is thrown, so all confirmed states
           * are valid
           */
          changeStateToNormal();
          responseWaitingTread.responseReceived(CollectionUtil.getTypedCollectionFromObject(
              state.getData(), IngredientAddition.class));
          break;
        case END:
          endHopCooking();
          break;
        default:
          break;
      }
    }

    /**
     * Finishes the hop cooking process and begins with the whirlpool
     */
    private void endHopCooking() {
      // End Temperature Logging after Hop Cooking, os200215.
      temperatureLogger.unsubscribe();

      changeStateToNormal();
      try {
        logService.log(new EndMessage(State.HOP_COOKING));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Could not log end of hop cooking process.", e);
      }
      try {
        logService.log(new StartMessage(State.WHIRLPOOL));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Could not log begin of whirlpool process.", e);
      }
      if (hopCooker != null) {
        try {
          hopCooker.finish();
        } catch (BrewingProcessNotFoundException e) {
          LOGGER.warn("HopCooker.finish() failed", e);
        }
      }
      sendWhirlpoolFinishedRequest();
    }

    /**
     * Requests the user to confirm the end of the manual whirlpool step
     */
    private void sendWhirlpoolFinishedRequest() {
      currentBrewingProcess.setState(BrewingState.Type.REQUEST, BrewingState.State.WHIRLPOOL,
          BrewingState.Position.END);
      sendConfirmationRequest();
    }
  }

  /**
   * This class deals with all actions within the lautering state
   */
  private final class LauteringStateHandler implements BrewingStateInterface {
    @Override
    public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
      /*
       * Lautering is a manual step, so only the end of it is confirmed by the user
       */
      if (BrewingState.Position.END.equals(state.getPosition())) {
        endLautering();
      }
    }

    /**
     * Finishes the lautering process and begins with the hop cooking
     */
    private void endLautering() {
      changeStateToNormal();
      try {
        logService.log(new EndMessage(State.LAUTERING));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Could not log end of hop cooking process.", e);
      }
      sendHopCookingStartRequest();
    }

    /**
     * Notifies the user that the hop cooking process can be started
     */
    private void sendHopCookingStartRequest() {
      currentBrewingProcess.setState(BrewingState.Type.REQUEST, BrewingState.State.HOP_COOKING,
          BrewingState.Position.START);
      sendConfirmationRequest();
    }
  }

  /**
   * This class deals with all actions within the mashing state
   */
  private final class MashingStateHandler implements BrewingStateInterface {
    @Override
    public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
      /*
       * The user confirms the start of the mashing process as well as the addition of malt or the
       * result of the iodine test. The masher then confirms the end of its working process
       */
      switch (state.getPosition()) {
        case START:
          changeStateToNormal();
          try {
            // Start Temperature Logger, os200215.
            temperatureLogger.subscribeToTemperatureController(temperatureService);

            masher.startAction(currentBrewingProcess.getRecipe().getMashingPlan());
            brewingProcessSummary.setStartTimeMashing(System.currentTimeMillis());
            logService.log(new StartMessage(State.MASHING));
          } catch (BrewingProcessException e) {
            throw new InvalidBrewingStepException(
                "Can not start mashing right now. Coming from BrewinProcessException: "
                    + e.getMessage());
          }
          break;
        case ADDING:
          masher.confirmState(state);
          /*
           * these statements are only reached of no exception is thrown, so all confirmed states
           * are valid
           */
          responseWaitingTread.responseReceived(CollectionUtil.getTypedCollectionFromObject(
              state.getData(), IngredientAddition.class));
          break;
        case ONGOING:
          masher.confirmState(state);
          break;
        case END:
          endMashing();
          break;
        case IODINE:
          masher.confirmState(state);
          break;
      }
    }

    /**
     * Finishes the mashing process and begins with the lautering
     */
    private void endMashing() {
      // End Temperature Logging at end of Mashing, os200215.
      temperatureLogger.unsubscribe();

      changeStateToNormal();
      try {
        logService.log(new EndMessage(State.MASHING));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Could not log end of mashing process.", e);
      }
      try {
        logService.log(new StartMessage(State.LAUTERING));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Could not log end of hop cooking process.", e);
      }
      try {
        masher.finish();
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Masher could not be finished", e);
      }
      sendLauteringFinishedRequest();
    }

    /**
     * Requests the user to confirm the end of the manual lautering step
     */
    private void sendLauteringFinishedRequest() {
      currentBrewingProcess.setState(BrewingState.Type.REQUEST, BrewingState.State.LAUTERING,
          BrewingState.Position.END);
      sendConfirmationRequest();
    }
  }

  /**
   * This class deals with all actions within the not-started state
   */
  private final class NotStartedStateHandler implements BrewingStateInterface {
    @Override
    public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
      /*
       * nothing to do here
       */
      return;
    }
  }

  private final class WhirlpoolStateHandler implements BrewingStateInterface {
    @Override
    public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
      /*
       * Whirlpool is a manual step, so only the end of it is confirmed by the user
       */
      if (BrewingState.Position.END.equals(state.getPosition())) {
        endWhirlpool();
      }
    }

    /**
     * Finishes the whirlpool process
     */
    private void endWhirlpool() {
      changeStateToNormal();
      try {
        logService.log(new EndMessage(State.WHIRLPOOL));
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.warn("Could not log end of whirlpool process.", e);
      }
      sendBrewingCompleteRequest();
    }

    /**
     * Finishes the brewing process by sending a final request to the client that asks for a
     * confirmation that no more steps are to be performed
     */
    private void sendBrewingCompleteRequest() {
      currentBrewingProcess.setState(BrewingState.Type.REQUEST, BrewingState.State.FINISHED,
          BrewingState.Position.END);
      sendConfirmationRequest();
    }
  }

  @Override
  public List<Message> getPushMessages() throws BrewingProcessNotFoundException {
    if (currentBrewingProcess == null) {
      throw new BrewingProcessNotFoundException("No brewing process ongoing");
    }
    return pushedMessages;
  }

  @Override
  public void actualizeTemperatureLevel(final TemperatureLevelInfo temperatureLevelInfo) {
    // ArrayList<TemperatureLevelInfo> temperatureLevelInfoList =
    // brewingProcessSummary.getTemperatureLevelInfo();
    // TemperatureLevelInfo lastTemperatureLevelInfo =
    // temperatureLevelInfoList.size() > 0 ? temperatureLevelInfoList.get(temperatureLevelInfoList
    // .size() - 1) : null;
    // if (lastTemperatureLevelInfo == null
    // || lastTemperatureLevelInfo.position != temperatureLevelInfo.position) {
    // temperatureLevelInfoList.add(temperatureLevelInfo);
    // } else {
    // lastTemperatureLevelInfo.alreadyStarted = true;
    // lastTemperatureLevelInfo.startTimeMillis = temperatureLevelInfo.startTimeMillis;
    // }
    brewingProcessSummary.getTemperatureLevelInfo().add(temperatureLevelInfo);
  }

	@Override
	public IAcousticNotifier getAcousticNotifier() {
		return this.acousticNotifier;
	}
}
