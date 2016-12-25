package impl;

import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.Type;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IBrewingPartPlan;
import interfaces.ITemperatureService;
import messages.Message;

import com.google.inject.Inject;
import com.google.inject.Provider;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;

/**
 * 
 * @author Patrick
 *
 *         Abstract class for the parts during the brewing process, i.e. Masher and HopCooker
 */
public abstract class BrewingPart {

  /** The temperature service. */
  protected final ITemperatureService temperatureService;

  /** The log service. */
  protected final IBrewingLogService logService;

  protected Provider<IBrewingController> brewingController;

  /**
   * Instantiates a BrewingPart.
   *
   * @param temperatureService the temperature service
   * @param brewingLogService the brewing log service
   * @param brewingService the brewing service
   */
  @Inject
  public BrewingPart(final ITemperatureService temperatureService,
      final IBrewingLogService brewingLogService,
      final Provider<IBrewingController> brewingController) {
    this.temperatureService = temperatureService;
    this.logService = brewingLogService;
    this.brewingController = brewingController;
  }

  /**
   * Notify listener.
   *
   * @param message the message to be sent to the listeners
   */
  protected void notifyListener(final Message message) {
    brewingController.get().notify(message);
  }


  /**
   * Notifies the current brewing part controller that the user has entered a confirmation that is
   * relevant to its process
   *
   * @param state the state
   * @throws InvalidBrewingStepException
   */
  public abstract void confirmState(BrewingState state) throws InvalidBrewingStepException;

  /**
   * Requests the confirmation for a new position in the process from the brewingListeners.
   * 
   * @param requestedState the position inside the current process to be confirmed
   * @throws BrewingProcessNotFoundException
   */
  protected void requestConfirmation(final Position requestedState)
      throws BrewingProcessNotFoundException {
    requestConfirmation(requestedState, Type.REQUEST, null);
  }

  /**
   * Initializes the end of a brewingPart's process.
   * 
   * @param requestedState the position inside the current process to be confirmed
   * @throws BrewingProcessNotFoundException
   */
  protected void requestInternConfirmation(final Position requestedState)
      throws BrewingProcessNotFoundException {
    requestConfirmation(requestedState, Type.INTERN, null);
  }

  /**
   * Sets the current brewing state inside the BrewingController to the specified position.
   * 
   * @param requestedState the specified position
   * @param data addition data to add to the current brewing state
   * @throws BrewingProcessNotFoundException
   */
  protected void requestConfirmation(final Position requestedState, final Object data)
      throws BrewingProcessNotFoundException {
    requestConfirmation(requestedState, Type.REQUEST, data);
  }

  /**
   * Sets the current brewing state inside the BrewingController to the specified position.
   * 
   * @param requestedState the specified position
   * @param requestedType the specified type
   * @param data addition data to add to the current brewing state
   * @throws BrewingProcessNotFoundException
   */
  protected void requestConfirmation(final Position requestedState, final Type requestedType,
      final Object data) throws BrewingProcessNotFoundException {
    brewingController.get().setRequestState(requestedState, requestedType, data);
  }

  /**
   * Changes the brewing state after receiving a confirmation.
   * 
   * @param requestedState the position inside the current process to be confirmed
   * @throws BrewingProcessNotFoundException
   */
  protected void proceedConfirmation(final Position confirmedState)
      throws BrewingProcessNotFoundException {
    brewingController.get().setConfirmedState(confirmedState);
  }

  /**
   * /** Confirms a position inside the current process
   * 
   * @param confirmedState
   * @throws BrewingProcessNotFoundException
   * @throws InvalidBrewingStepException
   */
  protected void confirmStep(final BrewingState confirmedState)
      throws BrewingProcessNotFoundException, InvalidBrewingStepException {
    brewingController.get().confirmStep(confirmedState);
  }

  /**
   * Finishes the mashing process. This is called by the BrewingController at the end of the brewing
   * process.
   * 
   * @throws BrewingProcessNotFoundException
   */
  public void finish() throws BrewingProcessNotFoundException {
    if (temperatureService != null) {
      temperatureService.stop();
    }
    logService.log(new Message(getClass() + " terminated."));
  }

  /**
   * Starts the corresponding process.
   *
   * @param brewingPartPlan the current recipe part for this process (HopCookingPlan or MashingPlan)
   */
  public abstract void startAction(IBrewingPartPlan brewingPartPlan) throws BrewingProcessException;
}
