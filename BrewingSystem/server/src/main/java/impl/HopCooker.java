/*
 *
 */
package impl;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.BrewingProcessSummary.TemperatureLevelInfo;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HopAddition;
import general.HopCookingPlan;
import interfaces.IAcousticNotifier;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IBrewingPartPlan;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import messages.ConfirmationRequestMessage;
import messages.HopAdditionMessage;
import messages.Message;
import messages.PreNotificationMessage;
import messages.TemperatureMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Performes the hop cooking process.
 */
@Singleton
public class HopCooker extends BrewingPart {

  /** The logger which saves all the information into log files */
  private static final Logger LOGGER = LogManager.getLogger();

  /** The hop cooking plan. */
  private HopCookingPlan hopCookingPlan;

  /** The hop additions that we expect to be confirmed with the next confirmation **/
  private final List<HopAddition> expectedHopAdditions = new ArrayList<>();

  /** The temperature that is used for hop cooking **/
  private final int hopcookingTemperature;
  /**
   * User gets notified about hop cooking start when current temperature is HOPCOOKING_TEMPERATURE -
   * HOPCOOKING_PRENOTIF_TEMPERATURE
   **/
  private final int hopcookingPrenotifTemperature;
  /** The time the user gets notified that he has to insert hop before the current addition **/
  private final long hopcookingPrenotifTime;

  /* amount of milliseconds to wait until timeout */
  private final long maximalWaitingTimeMillis;

  private Thread hopCookingThread;

  /**
   * Instantiates a new hop cooker.
   *
   * @param temperatureService the temperature service
   * @param brewingLogService the brewing log service
   */
  @Inject
  public HopCooker(final ITemperatureService temperatureService,
      final IBrewingLogService brewingLogService, final Provider<IBrewingController> listener) {
    super(temperatureService, brewingLogService, listener);
    maximalWaitingTimeMillis =
        PropertyUtil.getPropertyLong(PropertyUtil.MILLIS_TO_REQUEST_CONFIRMATION_TIMEOUT_PROPERTY);

    // Get some values from the settings file.
    hopcookingTemperature = (int) PropertyUtil.getHopCookingTemperature();
    hopcookingPrenotifTemperature = (int) PropertyUtil.getHopCookingPrenotificationTemperature();
    hopcookingPrenotifTime = (int) PropertyUtil.getHopCookingPrenotificationTimeMillis();
  }

  /**
   * To be called, when the user confirmed that he added hop. The hop addition will then be written
   * into the brewing log.
   * 
   * @throws InvalidBrewingStepException
   *
   * @see interfaces.IHopCooker#confirmState(general.BrewingState)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void confirmState(final BrewingState state) throws InvalidBrewingStepException {
    if (!(state.getPosition() == Position.ADDING && state.getState() == State.HOP_COOKING)) {
      throw new InvalidBrewingStepException("In invalid brewing state.");
    }
    if (state.getData() != null && state.getData() instanceof List<?>) {

      List<HopAddition> confirmedHAs;
      try {
        confirmedHAs = (List<HopAddition>) state.getData();
      } catch (Throwable t) {
        LOGGER.error("Invalid reply for hop addition confirmation", t);
        throw new InvalidBrewingStepException(
            "HopAddition confirmation does not contain a List<HopAddition>!");
      }

      // Important: Get an exclusive Lock!
      synchronized (expectedHopAdditions) {
        for (HopAddition confirmedHA : confirmedHAs) {
          if (expectedHopAdditions.remove(confirmedHA)) {
            HopAdditionMessage hm = new HopAdditionMessage(Arrays.asList(confirmedHA));
            try {
              logService.log(hm);
            } catch (BrewingProcessNotFoundException e) {
              LOGGER.error("Cannot write confirmed hop addition into log!", e);
            }
          } else {
            // Unexpected response:
            LOGGER.error("Confirmation for unexpected HopAddition '" + confirmedHA.getName()
                + "' received!");
            throw new InvalidBrewingStepException("Confirmation for unexpected HopAddition '"
                + confirmedHA.getName() + "' received!");
          }
        } // for
      } // synchronized
      if (expectedHopAdditions.isEmpty()) {
        try {
          proceedConfirmation(Position.ONGOING);
        } catch (BrewingProcessNotFoundException e) {
          LOGGER.error("Could proceed to brewing state position \"ONGOING\".", e);
        }
      }
    } else {
      LOGGER.error("Received Confirmation for hop addition with invalid data!");
    }
  }

  /**
   * Notifies the listeners that a hop addition must be performed right now.
   *
   * @param hopadditions
   */
  private void requestHopAdditionConfirmation(final List<HopAddition> hopadditions) {
    try {
      // Change status to request an ADDING of hop.
      this.requestConfirmation(Position.ADDING, Type.REQUEST, hopadditions);
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.error("Failed to request hop addition confirmation", e);
    }
    // Send message that requests confirmation.
    ConfirmationRequestMessage confirmationRequestMessage =
        new ConfirmationRequestMessage(new BrewingState(Type.REQUEST, State.HOP_COOKING,
            Position.ADDING, hopadditions));
    notifyListener(confirmationRequestMessage);
  }

  /**
   * Notifies the listeners that a hop addition soon needs to be performed.
   *
   * @param hopadditions
   */
  private void prenotifyHopAddition(final List<HopAddition> hopadditions, long millisToAddition) {
    BrewingState futureState =
        new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.ADDING, hopadditions);
    // Create prenotification with the brewing state that contains more information.
    PreNotificationMessage preNotificationMessage =
        new PreNotificationMessage(futureState, new HopAdditionMessage(hopadditions), millisToAddition);

    notifyListener(preNotificationMessage);
  }

  /**
   * Notifies the listeners that the hop cooking process will end shortly.
   */
  private void prenotifyHopcookingEnd() {
    Message endingMessage = new Message("The hop cooking process will end shortly.");
    notifyListener(endingMessage);
  }

  /**
   * Performs the steps that ends the hop cooking.
   */
  private void endHopcookingProcess() {

    // Turn off heater.
    if (temperatureService != null) {
      temperatureService.stop();
    }

    Message endMessage = new Message("Hop cooking process has ended.");
    notifyListener(endMessage);
    
    // Send a beep that notifies that hop cooking is ending.
    IAcousticNotifier notifier = this.brewingController.get().getAcousticNotifier();
    if (notifier != null) {
    	notifier.sendConfirmationRequestBeep();
    }

    try {
      // Request end of hop cooking.
      requestInternConfirmation(Position.END);
      // ... and perform confirmation by ourselves.
      confirmStep(new BrewingState(Type.INTERN, State.HOP_COOKING, Position.END));
    } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
      LOGGER.error("Error while ending hop cooking process", e);
    }
  }

  /**
   * Makes the calling thread sleep for the specified time in milliseconds if it is not zero. Errors
   * will be logged.
   *
   * @param timeMillis
   */
  private void sleepIfNotZero(final long timeMillis) {
    if (timeMillis > 0) {
      try {
        Thread.sleep(timeMillis);
      } catch (InterruptedException e) {
        LOGGER.error("Cannot sleep!", e);
      }
    }
  }

  /**
   * Starts hop cooking according to the specified hop cooking plan.
   *
   * @param plan the plan
   * @throws BrewingProcessException
   */
  @Override
  public void startAction(final IBrewingPartPlan hopCookingPlan) throws BrewingProcessException {
    if (hopCookingPlan == null) { /* if there is no hopCooking plan, skip this step */
      endHopcookingProcess();
    } else if (hopCookingPlan instanceof HopCookingPlan) {
      this.hopCookingPlan = (HopCookingPlan) hopCookingPlan;

      if (this.hopCookingPlan.getHopAdditions() == null
          || this.hopCookingPlan.getHopAdditions().isEmpty()) {
        /* if there are not hop additions, skip hop cooking */
        endHopcookingProcess();
      } else {
        // Send prenotification that hop cooking will start.
        temperatureService.subscribe(hopcookingTemperature, hopcookingPrenotifTemperature,
            new PrenotifyHopcookingStart(this));

        // Start hop cooking in new thread as soon as temperature is reached.
        temperatureService.subscribe(hopcookingTemperature, 1, new PerformHopcookingStart());

        brewingController.get().actualizeTemperatureLevel(
            new TemperatureLevelInfo(1, 0, ((HopCookingPlan) hopCookingPlan).getDuration(), 100,
                false, System.currentTimeMillis()));

        // Heat up to the hop cooking temperature.
        temperatureService.heatUp(hopcookingTemperature);
        proceedConfirmation(Position.ONGOING);
      }
    } else {
      throw new BrewingProcessException(
          "The given BrewingPartPlan is not an instance of HopCookingPlan");
    }
  }

  /**
   * TemperatureEvent listener that notifies the user that hop cooking temperature will be reached
   * shortly.
   *
   */
  private class PrenotifyHopcookingStart implements ITemperatureEvent {

    private final HopCooker parent;

    PrenotifyHopcookingStart(final HopCooker parent) {
      this.parent = parent;
    }

    @Override
    public SubscribeStatus temperatureReached(final float temperature) {

      if (hopCookingThread != null && hopCookingThread.isAlive()) {
        hopCookingThread.interrupt();
        hopCookingThread = null;
      }
      hopCookingThread = new Thread(new Runnable() {
        @Override
        public void run() {
          TemperatureMessage tempMsg = new TemperatureMessage(temperature);

          tempMsg.setMessage("Hop cooking temperature will be reached shortly.");

          parent.notifyListener(tempMsg);
        }
      });

      hopCookingThread.start();
      return SubscribeStatus.UNSUBSCRIBE;
    }
  }

  /**
   * TemperatureEvent listener that starts hop cooking in a new thread, when the temperature is
   * reached.
   *
   */
  private class PerformHopcookingStart implements ITemperatureEvent {

    @Override
    public SubscribeStatus temperatureReached(final float temperature) {
      hopCookingThread = new Thread(new Runnable() {

        @Override
        public void run() {
          brewingController.get().actualizeTemperatureLevel(
              new TemperatureLevelInfo(1, 0, hopCookingPlan.getDuration(), 100, true, System
                  .currentTimeMillis()));
          controlHopAdditions();
        }
      });

      hopCookingThread.start();

      return SubscribeStatus.UNSUBSCRIBE;
    }

    /**
     * Controls hop additions. Notifies user when he has to add hop.
     */
    private void controlHopAdditions() {

      // The current time in the hop addition sequence.
      long currentTime = 0;
      int i = 0;

      while (i < hopCookingPlan.getHopAdditions().size()) {
        // --Collect all hop additions that have the same input time-------------//
        HopAddition currHA = hopCookingPlan.getHopAdditions().get(i);
        synchronized (expectedHopAdditions) {
          // currHA wird auch erst in der Schleife hinzugefügt.
          while (i < hopCookingPlan.getHopAdditions().size()) {
            HopAddition nextHA = hopCookingPlan.getHopAdditions().get(i);
            if (nextHA.getInputTime() == currHA.getInputTime()) {
              expectedHopAdditions.add(nextHA);
              i++;
            } else {
              break;
            }
          }
        }
        // ---------------------------------------------------------------------//

        // Sleep until input time minus prenotification time.
        long sleepingTime =
            Math.max(currHA.getInputTime() - hopcookingPrenotifTime - currentTime, 0);
        sleepIfNotZero(sleepingTime);
        // Advance the amount of time that we have slept.
        currentTime += sleepingTime;

        // Calculate distance to next input time.
        sleepingTime = Math.max(currHA.getInputTime() - currentTime, 0);
        
        // Time to send the prenotification.
        // --> Pass a copy of the list to ensure thread-safety.
        // --> sleeping time is the time to the next input time.
        prenotifyHopAddition(new ArrayList<HopAddition>(expectedHopAdditions), sleepingTime);

        // Sleep until the actual input time.
        sleepIfNotZero(sleepingTime);
        // Advance the amount of time that we have slept.
        currentTime += sleepingTime;

        // Now hop addition has to be performed, send final request message!
        // --> Pass a copy of the list to ensure thread-safety.
        requestHopAdditionConfirmation(new ArrayList<HopAddition>(expectedHopAdditions));
      }

      long timeAfterLastHopAdditionRequest = System.currentTimeMillis();
      // Respect the cooking time of the last hop that is currently in the brewing,
      // again minus prenotification time.
      long sleepingTime =
          Math.max(hopCookingPlan.getDuration() - hopcookingPrenotifTime - currentTime, 0);
      sleepIfNotZero(sleepingTime);
      currentTime += sleepingTime;

      /* while there is still an open request and the time has not run out, wait */
      while (System.currentTimeMillis() - timeAfterLastHopAdditionRequest < maximalWaitingTimeMillis
          && !expectedHopAdditions.isEmpty()) {
        try {
          Thread.sleep(1500);
        } catch (InterruptedException e) {
          LOGGER.error("Hop Cooker could not sleep to wait for hop addition confirmation.", e);
        }
      }

      prenotifyHopcookingEnd(); // Prenotify the end of hop cooking.

      // Now sleep until the final end of the hop cooking process.
      sleepingTime = Math.max(hopCookingPlan.getDuration() - currentTime, 0);
      sleepIfNotZero(sleepingTime);
      currentTime += sleepingTime;

      // Perform final steps.
      endHopcookingProcess();
    }
  }

  @Override
  public void finish() {
    if (hopCookingThread != null && hopCookingThread.isAlive()) {
      hopCookingThread.interrupt();
      hopCookingThread = null;
    }
    try {
      super.finish();
    } catch (BrewingProcessNotFoundException e) {
      LOGGER.warn("super.finish() failed", e);
    }
  }
}
