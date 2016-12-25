/*
 * 
 */
package interfaces;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import general.ActuatorDetails;
import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingState;

import java.util.List;
import java.util.TreeMap;

import messages.Message;


/**
 * Provides methods which do not belong into the recipe-, protocol-, brewing- and message-service
 * but are necessary for the communication between components.
 *
 * @author Daniel Langerenken
 */
public interface IUserFacadeService extends IRecipeService, IProtocolService, IBrewingService,
    IMessageService {

  /**
   * Returns the current-actuator details (whether they are connected, enabled, disabled or
   * temperature for the temperature controller.
   *
   * @return class which just stores all actuator details
   */
  @Override
  ActuatorDetails getCurrentActuatorDetails();

  /**
   * Returns the current-brewingProcess if available.
   *
   * @return current brewing process - otherwise throws exception that process not found
   */
  @Override
  BrewingProcess getCurrentBrewingProcess();

  /**
   * Gets the recipe from recipe id and starts brewing afterwards.
   *
   * @param recipeId id of the recipe which should be started
   * @throws RecipeNotFoundException returned exception when id invalid
   * @throws BrewingProcessException returned exception when brewing process already ongoing
   * @throws RecipeParseException the recipe parse exception
   */
  void startBrewing(String recipeId) throws RecipeNotFoundException, BrewingProcessException,
      RecipeParseException;

  /**
   * Confirms the iodine-test
   * 
   * @param duration null, if test positive, not null if negative and needs to be repeated
   * @throws InvalidBrewingStepException iodine-test is not expected
   * @throws BrewingProcessNotFoundException no brewing process ongoing
   */
  void confirmDoIodineTest(Integer duration) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException;

  /**
   * Returns the current brewing state
   * 
   * @throws BrewingProcessNotFoundException - no brewing process ongoing
   * @return current brewing state or brewingprocessnotfound-exception if failed
   */
  BrewingState getCurrentBrewingState() throws BrewingProcessNotFoundException;

  /**
   * Returns a list of messages which are newer than a given date
   * 
   * @param since timestamp which is used for receiving only newer messages
   * @throws BrewingProcessNotFoundException - no brewing process ongoing
   * @return List of Messages which are newer than the given time stamp
   */
  List<Message> getCurrentMessagesSince(Long since) throws BrewingProcessNotFoundException;;

  /**
   * Returns a treemap (sorted by time of recording as Long) of temperatures which are newer than a
   * given date
   * 
   * @param since timestamp which is used for receiving only newer temperatures, passing null
   *        returns all temperatures
   * @throws BrewingProcessNotFoundException - no brewing process ongoing
   * @return Map of TimeStamp and corresponding temperature which are newer than the given time
   *         stamp
   */
  TreeMap<Long, Float> getTemperaturesSince(Long since) throws BrewingProcessNotFoundException;

  /**
   * Returns a summary of the current brewing process
   * 
   * @throws BrewingProcessNotFoundException - no brewing process ongoing
   * @return wrapper object which contains basic information about the process
   */
  @Override
  BrewingProcessSummary getCurrentBrewingProcessSummary() throws BrewingProcessNotFoundException;

  /**
   * Returns the list of current push messages of the brewing process
   * 
   * @return list of all push messages which belong to the latest brewing process
   * @throws BrewingProcessNotFoundException
   */
  List<Message> getPushMessages() throws BrewingProcessNotFoundException;
}
