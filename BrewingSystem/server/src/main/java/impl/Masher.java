package impl;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.BrewingProcess;
import general.BrewingProcessSummary.TemperatureLevelInfo;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.IodineTest;
import general.MaltAddition;
import general.MashingPlan;
import general.Pair;
import general.TemperatureLevel;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IBrewingPartPlan;
import interfaces.IStirrerService;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import messages.ConfirmationRequestMessage;
import messages.IodineTestMessage;
import messages.MaltAdditionMessage;
import messages.PreNotificationMessage;
import messages.TemperatureLevelMessage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.CollectionUtil;
import utilities.PropertyUtil;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * This class is used for the mashing process within the brewing process
 */
@Singleton
public class Masher extends BrewingPart {

  /**
   * The Delta which is allowed during the mashing (degrees)
   */
  public final static int MASHING_DELTA = 5;

  /**
   * The Iodine-State as constant for reusing purposes
   */
  public final static BrewingState IODINE_STATE = new BrewingState(BrewingState.Type.REQUEST,
      BrewingState.State.MASHING, BrewingState.Position.IODINE);

  /**
   * The Malt-State as constant for reusing purposes
   */
  public final static BrewingState MALT_ADDITION_STATE = new BrewingState(
      BrewingState.Type.REQUEST, BrewingState.State.MASHING, BrewingState.Position.ADDING);

  /**
   * An enum which shows the next action which has to be done by the masher
   */
  private enum NextAction {
    PrenotifyMalt, NotifyMalt, NextTemp
  }

  /**
   * Parallel thread which deals with a delayed iodine-test
   */
  private class IodineTestWaiter extends Thread {
    
    /**
     * time to wait until the next iodine test can be done
     */
    final long timeToWait;

    /**
     * Initializes the Thread with a time to wait
     * @param timeToWait the time in milliseconds to wait
     */
    IodineTestWaiter(final long timeToWait) {
      this.timeToWait = timeToWait;
    }

    @Override
    public void run() {
      sleepMasher(timeToWait);
      startIodineTest();
    }
  }

  private class NextActionWaiter extends Thread {
    final long timeToWait;
    final MaltAddition maltAdditionToRequest;
    final ListIterator<ActionAndTime> actionIterator;

    NextActionWaiter(final long timeToWait, final MaltAddition maltAdditionToRequest,
        final ListIterator<ActionAndTime> actionIterator) {
      this.timeToWait = timeToWait;
      this.maltAdditionToRequest = maltAdditionToRequest;
      this.actionIterator = actionIterator;
    }

    @Override
    public void run() {
      sleepMasher(timeToWait);
      requestedMaltAdditions.add(maltAdditionToRequest);
      notifyListener(new MaltAdditionMessage(
          CollectionUtil.getArrayListOfBrewingStatesSortedByTime(requestedMaltAdditions)));
      controlMaltAdditions(actionIterator);
    }
  }

  private class ActionAndTime {
    NextAction nextAction;
    Long time;
    MaltAddition maltAddition;
    TemperatureLevel tempLevel;

    ActionAndTime(final NextAction nextAction, final Long time) {
      this.nextAction = nextAction;
      this.time = time;
    }
  }

  private static class Timer {
    private static boolean running = false;
    private static long startTime = -1;
    private static long alreadyPassedTime = 0;

    /**
     * resets the Timer
     */
    static void reset() {
      startTime = -1;
      alreadyPassedTime = 0;
      running = false;
    }

    /**
     * starts or resstarts a timer
     */
    static void start() {
      if (!running) {
        startTime = System.currentTimeMillis();
        running = true;
      }
    }

    /**
     * stops the timer and saves the time passed to the given point in time, so that it can be
     * restarted
     */
    static void stop() {
      if (running) {
        alreadyPassedTime += System.currentTimeMillis() - startTime;
        running = false;
      }
    }

    /**
     * @return the passed time since the timer was reseted not counting the time it was stoped
     */
    static long getTimeMillis() {
      return running ? (System.currentTimeMillis() - startTime + alreadyPassedTime)
          : alreadyPassedTime;
    }
  }

  /** The Constant logger. */
  public static final Logger LOGGER = LogManager.getLogger();

  private final BrewingState brewingState;
  private final LinkedList<ActionAndTime> sortedActions;
  private TemperatureLevel lastTempLevel;
  private ITemperatureEvent currentTempEvent;
  private MashingPlan mashingPlan;
  private final IStirrerService stirrer;
  private final Set<MaltAddition> requestedMaltAdditions;
  private boolean maltAdditionsFinished = false;
  private long millisToNotification;
  private IodineTestWaiter iodineTestWaiter;
  private NextActionWaiter nextActionWaiter;
  private Thread mashingThread;

  /**
   * Instantiates a new Masher.
   *
   * @param temperatureService the temperature service
   * @param brewingLogService the brewing log service
   * @param brewingService the brewing service
   */
  @Inject
  public Masher(final ITemperatureService temperatureService, final IStirrerService stirrer,
      final IBrewingLogService brewingLogService,
      final Provider<IBrewingController> brewingController) {
    super(temperatureService, brewingLogService, brewingController);
    this.stirrer = stirrer;
    sortedActions = new LinkedList<ActionAndTime>();
    requestedMaltAdditions = new HashSet<MaltAddition>();
    brewingState =
        new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.MASHING,
            BrewingState.Position.ONGOING);
    init();
  }

  /**
   * Initializes a Masher by setting the brewingState to mashing start, mashingPlan to null,
   * clearing sortedActions and requestedMaltAdditions
   */
  private void init() {
    brewingState.setData(Type.NORMAL);
    brewingState.setState(State.MASHING);
    brewingState.setPosition(Position.ONGOING);
    mashingPlan = null;
    sortedActions.clear();
    requestedMaltAdditions.clear();
    currentTempEvent = null;
    millisToNotification =
        PropertyUtil.getPropertyLong(PropertyUtil.MILLIS_TO_NOTIFICATION_PROPERY);
  }

  @Override
  public void finish() throws BrewingProcessNotFoundException {
    if (stirrer != null) {
      stirrer.stopStirring();
    }
    if (mashingThread != null && mashingThread.isAlive()) {
      mashingThread.interrupt();
      mashingThread = null;
    }
    if (iodineTestWaiter != null && iodineTestWaiter.isAlive()) {
      iodineTestWaiter.interrupt();
      iodineTestWaiter = null;
    }
    if (nextActionWaiter != null && nextActionWaiter.isAlive()) {
      nextActionWaiter.interrupt();
      nextActionWaiter = null;
    }
    super.finish();
    init();
  }

  /**
   * This responds to: Malt addition confirmation Iodine test confirmation
   * 
   * @throws InvalidBrewingStepException
   */
  @Override
  public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
    if (BrewingState.State.MASHING == state.getState() // correct brewing phase
        && BrewingState.Type.REQUEST == state.getType()) {
      if (BrewingState.Position.ADDING == state.getPosition()) { // malt was added
        addingConfirmation(state);
      } else if (BrewingState.Position.IODINE.equals(state.getPosition())) { // iodine test was
                                                                             // performed
        iodineConfirmation(state);
      }
    }
  }

  /**
   * Performs the actions for a malt addition confirmation.
   * 
   * @param state
   * @throws InvalidBrewingStepException
   */
  private void addingConfirmation(final BrewingState state) throws InvalidBrewingStepException {
    checkMaltAdditionConfirmationAgainstRequest(state.getData());
    try {
      logService.log(new MaltAdditionMessage(requestedMaltAdditions));
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.warn("Could not log the ingredient addition for " + requestedMaltAdditions + ".", e);
    }

    /* if everything requested has been confirmed, set state to ongoing */
    if (requestedMaltAdditions.isEmpty()) {
      LOGGER.info("Masher: mashing request ok");
      if (maltAdditionsFinished) {
        LOGGER.info("Masher: Mashing time finished");
        sleepUntilIodineTest(lastTempLevel.getStartTime() + lastTempLevel.getDuration()
            - Timer.getTimeMillis());
      } else {
        try {
          proceedConfirmation(Position.ONGOING);
        } catch (BrewingProcessNotFoundException e) {
          LOGGER.error("Could proceed to brewing state position \"ONGOING\".", e);
        }
      }
    } else /* else send another confirmation request for the rest */{
      notifyListener(new MaltAdditionMessage(
          CollectionUtil.getArrayListOfBrewingStatesSortedByTime(requestedMaltAdditions)));
    }
  }

  /**
   * Checks whether the list of confirmed malt additions is part of the requested malt additions. If
   * not, an exception is thrown. If so, the confirmed additions are removed from the requested ones
   * and if there are requests left a new IngredientAddition message is sent.
   * 
   * @param confirmationData
   * @throws InvalidBrewingStepException
   */
  private void checkMaltAdditionConfirmationAgainstRequest(final Object confirmationData)
      throws InvalidBrewingStepException {
    /* extract the confirmed malt additions from the data object */
    if (confirmationData == null || !(confirmationData instanceof List<?>)) {
      /* check the data object type */
      throw new InvalidBrewingStepException(
          "Not valid data-object. Must be a list of MaltAddition.");
    }
    List<?> listObjects = (List<?>) confirmationData;
    List<MaltAddition> maltAdditions = new ArrayList<MaltAddition>(listObjects.size());
    /* make sure all the elements are of type MaltAddition */
    for (Object o : listObjects) {
      if (!(o instanceof MaltAddition)) {
        throw new InvalidBrewingStepException(
            "Not valid data-object. Must be a list of MaltAddition.");
      } else {
        maltAdditions.add((MaltAddition) o);
      }
    }
    /* check if all confirmed additions have been requested */
    if (!requestedMaltAdditions.containsAll(maltAdditions)) {
      /* if the confirmation failed, send another request */
      // notifyListener(new IngredientAdditionMessage(requestedMaltAdditions));
      throw new InvalidBrewingStepException(
          "Some of the confirmed ingredients have not been requested.");
    }
    /* remove all the confirmed additions from the requested set */
    requestedMaltAdditions.removeAll(maltAdditions);
  }

  /**
   * Performs the actions for a iodine test confirmation
   * 
   * @param state
   * @throws InvalidBrewingStepException
   */
  private void iodineConfirmation(final BrewingState state) throws InvalidBrewingStepException {
    Object data = state.getData();
    if (data == null || !(data instanceof IodineTest)) {
      throw new InvalidBrewingStepException("Not valid data-object. Must be of type IodineTest.");
    }
    IodineTest test = (IodineTest) data;
    try {
      logService.log(new IodineTestMessage(test));
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.warn("Could not log the ingredient addition for " + requestedMaltAdditions + ".", e);
    }
    try {
      proceedConfirmation(Position.IODINE);
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.error("Could proceed to brewing state position \"IODINE\".", e);
    }
    if (test.isPositive()) {
      endIodineTest();
    } else {
      long timeToWait = test.getWaitingPeriod() * 1000; // getWaitingPeriod is in seconds
      sleepUntilIodineTest(timeToWait);
    }
  }

  @Override
  public void startAction(final IBrewingPartPlan newMashingPlan) throws BrewingProcessException {
    if (mashingPlan != null) {
      // only error handling for already existing mashing plan
      // checking the new mashingPlan for validation is already done in the BrewingController
      throw new BrewingProcessException("Another mashing process is already running");
    }
    if (newMashingPlan == null) { /* if there is no mashingPlan, skip mashing */
      endIodineTest();
    } else if (newMashingPlan instanceof MashingPlan) {
      mashingPlan = (MashingPlan) newMashingPlan;
      if (mashingPlan.getMaltAdditions() == null || mashingPlan.getMaltAdditions().isEmpty()) {
        /* if there are not malt additions, skip mashing */
        endIodineTest();
      } else {
        sortActionsByTime(mashingPlan.getTemperatureLevels(), mashingPlan.getMaltAdditions());
        try {
          proceedConfirmation(Position.ONGOING);
          stirrer.startStirring();
        } catch (BrewingProcessNotFoundException e) {
          LOGGER.error("Could proceed to brewing state position \"ONGOING\".", e.getMessage());
        }
        Timer.reset();
        controlTemperatureLevel(mashingPlan.getTemperatureLevels().get(0),
            sortedActions.listIterator()); // the first element
        // always exists
      }
    }
  }

  /**
   * Sorts the different steps during the mashing process that require notifications. Those are: - a
   * new temperature level is reached - malt has to be added - prenotification that malt has to be
   * added Those are put to a linked list in their order of start time
   * 
   * @param tempLevels the temperature levels as specified in the recipe
   * @param maltAdditions the malt additions as specified in the recipe
   */
  private void sortActionsByTime(final List<TemperatureLevel> tempLevels,
      final List<MaltAddition> maltAdditions) {
    LinkedList<Pair<Long, MaltAddition>> maltAdditionPrenotifications =
        new LinkedList<Pair<Long, MaltAddition>>();
    for (MaltAddition m : maltAdditions) {
      maltAdditionPrenotifications.add(new Pair<Long, MaltAddition>(Long.valueOf(m.getInputTime()
          - millisToNotification), m));
    }
    ListIterator<TemperatureLevel> tempIterator = mashingPlan.getTemperatureLevels().listIterator();
    ListIterator<MaltAddition> maltIterator = mashingPlan.getMaltAdditions().listIterator();
    ListIterator<Pair<Long, MaltAddition>> maltPrenotification =
        maltAdditionPrenotifications.listIterator();
    // iterator.hasNext() is true, because the BrewingController checks the mashingPlan to have at
    // least one temperature level to be valid
    tempIterator.next(); // do not add the first tempLevel, this is used as default initiation
    TemperatureLevel currentTemp = tempIterator.hasNext() ? tempIterator.next() : null;
    MaltAddition currentMalt = maltIterator.next();
    Pair<Long, MaltAddition> currentPrenotificationTime = maltPrenotification.next();
    while (currentTemp != null || currentMalt != null || currentPrenotificationTime != null) {
      if (currentPrenotificationTime != null // itself is not null
          && (currentTemp == null
              && currentMalt == null // both others are null
              || currentTemp == null
              && currentPrenotificationTime.getFirst().longValue() <= currentMalt.getInputTime() // one
                                                                                                 // is
              // null,
              // the other
              // not earlier
              || currentMalt == null
              && currentPrenotificationTime.getFirst().longValue() <= currentTemp.getStartTime() // one
                                                                                                 // is
          // null,
          // the other
          // not earlier
          || currentMalt != null && currentTemp != null // everything is not null
              && currentPrenotificationTime.getFirst().longValue() <= currentMalt.getInputTime() // this
                                                                                                 // is
                                                                                                 // a
              // smallest
              && currentPrenotificationTime.getFirst().longValue() <= currentTemp.getStartTime())) {
        ActionAndTime actionAndTime =
            new ActionAndTime(NextAction.PrenotifyMalt, currentPrenotificationTime.getFirst());
        actionAndTime.maltAddition = currentPrenotificationTime.getSecond();
        sortedActions.add(actionAndTime);
        currentPrenotificationTime =
            maltPrenotification.hasNext() ? maltPrenotification.next() : null;
      } else if (currentMalt != null // itself is not null
          && (currentTemp == null
              && currentPrenotificationTime == null // both others are null
              || currentTemp == null
              && currentMalt.getInputTime() <= currentPrenotificationTime.getFirst().longValue() // one
                                                                                                 // is
              // null,
              // the other
              // not earlier
              || currentPrenotificationTime == null
              && currentMalt.getInputTime() <= currentTemp.getStartTime() // one is null,
                                                                          // the other
                                                                          // not earlier
          || currentPrenotificationTime != null && currentTemp != null // everything is not null
              && currentMalt.getInputTime() <= currentPrenotificationTime.getFirst().longValue() // this
                                                                                                 // is
                                                                                                 // a
              // smallest
              && currentMalt.getInputTime() <= currentTemp.getStartTime())) {
        ActionAndTime actionAndTime =
            new ActionAndTime(NextAction.NotifyMalt, currentMalt.getInputTime());
        actionAndTime.maltAddition = currentMalt;
        sortedActions.add(actionAndTime);
        currentMalt = maltIterator.hasNext() ? maltIterator.next() : null;
      } else {
        // it is not maltPrenotification neither maltNotification
        ActionAndTime actionAndTime =
            new ActionAndTime(NextAction.NextTemp, currentTemp.getStartTime());
        sortedActions.add(actionAndTime);
        actionAndTime.tempLevel = currentTemp;
        currentTemp = tempIterator.hasNext() ? tempIterator.next() : null;
      }
    }
  }

  /**
   * This tells the heater to heat up to a specified temperature given in currentTempLevel and then
   * perform the notifications left in the linked list represented by the iterator
   * 
   * @param currentTempLevel current temperature to be reached
   * @param iterator for sortedActions that stores the current point during the mashing process
   */
  private void controlTemperatureLevel(final TemperatureLevel currentTempLevel,
      final ListIterator<ActionAndTime> iterator) {
    lastTempLevel = currentTempLevel;
    int tempInt = Math.round(currentTempLevel.getTemperature());
    Timer.stop();
    if (currentTempEvent != null) {
      temperatureService.unsubscribe(currentTempEvent);
    }
    currentTempEvent = new ITemperatureEvent() {

      @Override
      public SubscribeStatus temperatureReached(final float temperature) {
        performTemperatureReachedAction(currentTempLevel, iterator);
        return SubscribeStatus.UNSUBSCRIBE;
      }
    };
    temperatureService.subscribe(tempInt, MASHING_DELTA, currentTempEvent);
    brewingController.get().actualizeTemperatureLevel(
        new TemperatureLevelInfo(mashingPlan.getTemperatureLevels().indexOf(currentTempLevel) + 1,
            currentTempLevel.getStartTime(), currentTempLevel.getDuration(), currentTempLevel
                .getTemperature(), false, System.currentTimeMillis()));
    temperatureService.heatUp(tempInt);
  }

  /**
   * If there is still a brewing process ongoing and in mashing step, do: Actualizes the current
   * temperature level for the client, logs a temperature level message for the reached temperature
   * level, and starts a mashing thread to continue with malt additions
   * 
   * @param currentTempLevel the temperature level that was reached
   * @param iterator the iterator of the still pending malt additions and temperature levels
   */
  private void performTemperatureReachedAction(final TemperatureLevel currentTempLevel,
      final ListIterator<ActionAndTime> iterator) {
    if (brewingController == null || brewingController.get() == null) {
      LOGGER
          .info("Could not continue mashing process, because the brewing controller is null.\nState:\n\ttemperature level: "
              + currentTempLevel.getStartTime()
              + " time, "
              + currentTempLevel.getTemperature()
              + " temp");
      return;
    }
    BrewingProcess brewingProcess = brewingController.get().getCurrentBrewingProcess();
    if (brewingProcess == null || brewingProcess.getState() == null
        || brewingProcess.getState().getState() != State.MASHING) {
      LOGGER
          .info("Could not continue mashing process, because there is no brewing process ongoing, that is in the mashing state.\nState:\n\ttemperature level: "
              + currentTempLevel.getStartTime()
              + " time, "
              + currentTempLevel.getTemperature()
              + " temp");
      return;
    }

    Timer.start();
    brewingController.get().actualizeTemperatureLevel(
        new TemperatureLevelInfo(mashingPlan.getTemperatureLevels().indexOf(currentTempLevel) + 1,
            currentTempLevel.getStartTime(), currentTempLevel.getDuration(), currentTempLevel
                .getTemperature(), true, System.currentTimeMillis()));
    try {
      logService.log(new TemperatureLevelMessage(currentTempLevel));
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.warn("Could not log the temperature level reached.", e);
    }
    /* there is at max one mashingThread running, to control the malt addition requests */
    if (mashingThread != null && mashingThread.isAlive()) {
      mashingThread.interrupt();
      try {
        mashingThread.join();
      } catch (InterruptedException e) {
        LOGGER.error("MashingThread failed while join()", e);
      }
    }
    mashingThread = new Thread(new Runnable() {

      @Override
      public void run() {
        controlMaltAdditions(iterator);
      }
    });

    mashingThread.start();
  }

  /**
   * Loops over the remaining notifications in sortedActions and performs the malt addition
   * notifications and prenotifications. Steps back to controlTemperatureLevel if the time of a new
   * heating level has come. Waits for a user confirmation if a malt addition level has come.
   * 
   * @param iterator represents sortedActions
   */
  private void controlMaltAdditions(final ListIterator<ActionAndTime> iterator) {
    Timer.start();
    ActionAndTime nextActionAndTime = iterator.hasNext() ? iterator.next() : null;
    while (nextActionAndTime != null && NextAction.NextTemp != nextActionAndTime.nextAction) {
      /* handle malt addition notification and prenotifications here */
      if (NextAction.PrenotifyMalt == nextActionAndTime.nextAction) {
        /* prenotificiation */
        long timeToSleep = nextActionAndTime.time - Timer.getTimeMillis();
        PreNotificationMessage preNotification =
            new PreNotificationMessage(brewingState, new MaltAdditionMessage(
                Arrays.asList(nextActionAndTime.maltAddition)), millisToNotification);
        if (timeToSleep < 0) { // the notification is late
          if (timeToSleep + millisToNotification >= 0) {
            // it can be safed by sending a notification with less than the default delay
            preNotification.setMillisToNotification(timeToSleep + millisToNotification);
            notifyListener(preNotification);
          }
          nextActionAndTime = iterator.hasNext() ? iterator.next() : null;
        } else {
          try {
            Thread.sleep(timeToSleep);
          } catch (InterruptedException e) {
            // if the thread can not be put to sleep, send the pre-notification message earlier
            preNotification.setMillisToNotification(millisToNotification + timeToSleep);
            // skip the already sent notification
          } finally {
            notifyListener(preNotification);
            nextActionAndTime = iterator.hasNext() ? iterator.next() : null;
          }
        }
      } else {
        sleepUntilNextAction(nextActionAndTime.time - Timer.getTimeMillis(),
            nextActionAndTime.maltAddition, iterator);
        return;
      }
    }
    if (nextActionAndTime != null) {
      sleepMasher(nextActionAndTime.time - Timer.getTimeMillis());
      controlTemperatureLevel(nextActionAndTime.tempLevel, iterator);
    } else { /* the last mashing step has been performed */
      LOGGER.info("Masher: no more temp levels");
      if (requestedMaltAdditions.isEmpty()) {
        LOGGER.info("Masher: sleeping for "
            + (lastTempLevel.getStartTime() + lastTempLevel.getDuration() - Timer.getTimeMillis())
            + "millis");
        /* wait until end of temperature level, then start iodine test */
        sleepUntilIodineTest(lastTempLevel.getStartTime() + lastTempLevel.getDuration()
            - Timer.getTimeMillis());
      } else /* there are still malt additions requested */{
        LOGGER.info("Masher: mashing requests pending");
        maltAdditionsFinished = true;
        /* send a notification to remind the brewer to add the missing malts */
        notifyListener(new MaltAdditionMessage(
            CollectionUtil.getArrayListOfBrewingStatesSortedByTime(requestedMaltAdditions)));
      }
    }
  }

  /**
   * Tries to sleep for the given amount of time with a maximum of 50 failed tries
   * 
   * @param timeToSleep
   */
  private void sleepMasher(final long timeToSleep) {
    long sleepTime = timeToSleep;
    long systemTime;
    int counter = 1;
    while (sleepTime > 0 && counter < 50) {
      systemTime = System.currentTimeMillis();
      try {
        Thread.sleep(timeToSleep);
        sleepTime = 0; // exit loop
      } catch (InterruptedException e) {
        LOGGER.warn("Failed sleep try: " + counter++, e);
        /* subtract the passed time from the new sleeping time */
        sleepTime -= (System.currentTimeMillis() - systemTime);
        counter++;
      }
    }
    if (counter >= 50) {
      LOGGER.error("Can not put threads to sleep. Brewing process speed up.");
    }
  }

  /**
   * Puts the thread to sleep until the next action is performed.
   * 
   * @param timeToWait time to wait
   * @param maltAddition the next malt addition to be requested
   * @param iterator the iterator of the sorted list of actions to perform to save the current state
   *        of the iteration
   */
  private void sleepUntilNextAction(final long timeToWait, final MaltAddition maltAddition,
      final ListIterator<ActionAndTime> iterator) {
    if (nextActionWaiter != null && nextActionWaiter.isAlive()) {
      nextActionWaiter.interrupt();
    }
    nextActionWaiter = new NextActionWaiter(timeToWait, maltAddition, iterator);
    nextActionWaiter.start();
  }

  /**
   * Puts the thread to sleep until the next iodine test is performed.
   * 
   * @param timeToWait time to wait
   */
  private void sleepUntilIodineTest(final long timeToWait) {
    if (iodineTestWaiter != null && iodineTestWaiter.isAlive()) {
      iodineTestWaiter.interrupt();
    }
    iodineTestWaiter = new IodineTestWaiter(timeToWait);
    iodineTestWaiter.start();
  }

  /**
   * Starts a new iodine test by notifying the user to perform the test and enter a result
   */
  private void startIodineTest() {
    try {
      requestConfirmation(Position.IODINE);
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.error("Could not requested a iodine test.", e);
    }
    super.notifyListener(new ConfirmationRequestMessage(IODINE_STATE));
  }

  /**
   * Ends the iodine test phase and there for the last phase of the mashing process. Turns off the
   * heater and notifies the BrewingConroller.
   */
  private void endIodineTest() {
    if (temperatureService != null) {
      temperatureService.stop();
    }
    if (currentTempEvent != null) {
      temperatureService.unsubscribe(currentTempEvent);
      stirrer.stopStirring();
    }
    try {
      requestInternConfirmation(Position.END);
      confirmStep(new BrewingState(BrewingState.Type.INTERN, BrewingState.State.MASHING,
          BrewingState.Position.END));
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.error("Could not requested the end of the mashing process.", e);
    } catch (InvalidBrewingStepException e) {
      LOGGER.error("Could not confirm the end of the mashing process.", e);
    }
  }

  /**
   * Notify listener.
   *
   * @param mashingMessage the mashing message to be sent
   */
  private void notifyListener(final MaltAdditionMessage mashingMessage) {
    try {
      requestConfirmation(MALT_ADDITION_STATE.getPosition(), mashingMessage.getMaltAdditions());
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.log(Level.ERROR,
          "Could not requested the ingredient addition for " + mashingMessage.getMaltAdditions()
              + "." + e.getMessage());
    }
    super.notifyListener(mashingMessage);
    BrewingState state = BrewingState.fromValue(MALT_ADDITION_STATE.toValue());
    state.setData(mashingMessage.getMaltAdditions());
    super.notifyListener(new ConfirmationRequestMessage(state));
  }

  /**
   * Notify listener.
   *
   * @param preMashingMessage the pre-notification containing a mashing message
   */
  private void notifyListener(final PreNotificationMessage preMashingMessage) {
    super.notifyListener(preMashingMessage);
  }
}
