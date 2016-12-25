package interfaces;

import java.util.List;

import messages.Message;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.BrewingProcessSummary.TemperatureLevelInfo;
import general.BrewingState;
import general.BrewingState.Type;

/**
 * Interface for the BrewingController
 */
public interface IBrewingController extends IBrewingService {

	/**
	 * Gets the acoustic notifier that controls the signal pin.
	 * @return
	 */
	IAcousticNotifier getAcousticNotifier();
	
  /**
   * Requests the confirmation of a state.
   * 
   * @param requestedState
   * @param requestedType
   * @throws BrewingProcessNotFoundException if there is no running brewing process
   */
  void setRequestState(final BrewingState.Position requestedState, final Type requestedType,
      Object data) throws BrewingProcessNotFoundException;

  /**
   * Sets the brewing service into a state after getting the concurrent confirmation.
   * 
   * @param confirmedState
   */
  public void setConfirmedState(BrewingState.Position confirmedState)
      throws BrewingProcessNotFoundException;


  /**
   * Handles the confirmation of a specified BrewingState
   * 
   * @param confirmedState the BrewingState that is to be confirmed
   */
  @Override
  void confirmStep(final BrewingState confirmedState) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException;


  /**
   * Notify.
   *
   * @param hopCookingMessage the hop cooking message
   */
  void notify(Message message);

  /**
   * Returns the list of current brewing push message
   * 
   * @return current push messages of the brewing process
   */
  public List<Message> getPushMessages() throws BrewingProcessNotFoundException;

  /**
   * Refreshes the temperature level
   * 
   * @param temperatureLevelInfo new temperature level which should be updated to
   */
  public void actualizeTemperatureLevel(TemperatureLevelInfo temperatureLevelInfo);
}
