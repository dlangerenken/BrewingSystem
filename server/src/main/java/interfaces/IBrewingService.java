/*
 * 
 */
package interfaces;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.ActuatorDetails;
import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingState;
import general.Recipe;


/**
 * The Interface IBrewingService.
 */
public interface IBrewingService {

  /**
   * Confirm step.
   *
   * @param brewingState the brewing state
   * @throws BrewingProcessNotFoundException the brewing process not found exception
   * @throws InvalidBrewingStepException the invalid brewing step exception
   */
  void confirmStep(BrewingState brewingState) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException;

  /**
   * Current status of the connected/not connected hardware.
   *
   * @return information about heater, stirrer, temperature-sensor and temperature
   */
  ActuatorDetails getCurrentActuatorDetails();

  /**
   * Gets the current brewing process.
   *
   * @return the current brewing process
   */
  BrewingProcess getCurrentBrewingProcess();

  /**
   * Start brewing.
   *
   * @param r the r
   * @throws BrewingProcessNotFoundException no brewing process ongoing
   */
  void startBrewing(Recipe r) throws BrewingProcessException;

  /**
   * Cancels the current brewing process
   * 
   * @throws BrewingProcessNotFoundException no brewing process ongoing
   */
  void cancelCurrentBrewingProcess() throws BrewingProcessNotFoundException;


  /**
   * Returns a summary of the current brewing process
   * 
   * @throws BrewingProcessNotFoundException no brewing process ongoing
   * @return summary of the current brewing process
   */
  BrewingProcessSummary getCurrentBrewingProcessSummary() throws BrewingProcessNotFoundException;
}
