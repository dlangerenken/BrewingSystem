/*
 *
 */
package impl;

import general.ActuatorDetails;
import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.IodineTest;
import general.LogSummary;
import general.Protocol;
import general.Recipe;
import general.RecipeSummary;
import interfaces.IBrewingController;
import interfaces.IMessageService;
import interfaces.IProtocolService;
import interfaces.IRecipeService;
import interfaces.IUserFacadeService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import messages.Message;
import messages.TemperatureMessage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import push.PushMessage;
import push.PushType;

import com.google.inject.Singleton;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;


/**
 * The UserFacade acts as a facade to the user/network and system. Therefore every call from the
 * network and to the network runs over this facade which redirects the request to the appropriate
 * services
 *
 * @author Daniel Langerenken
 *
 */
@Singleton
public class UserFacade implements IMessageService, IUserFacadeService {

  /** Static Logger instance which logs every necessary information. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** RecipeService which deals with every information about recipes. */
  private final IRecipeService recipeService;

  /** ProtocolService which deals with every information about protocols. */
  private final IProtocolService protocolService;

  /** BrewingService which deals with every information about the brewing. */
  private final IBrewingController brewingService;

  /** MessageService which notifies via push-message subscribed clients. */
  private final IMessageService messageService;

  /**
   * Creates the user-facade with all dependencies.
   *
   * @param messageService MessageService which is used for communication with the clients
   * @param brewingService BrewingService which deals with the whole brewing process
   * @param protocolService ProtocolService which stores and offers protocols
   * @param recipeService RecipeService which stores and offers recipes
   */
  @Inject
  public UserFacade(final IMessageService messageService, final IBrewingController brewingService,
      final IProtocolService protocolService, final IRecipeService recipeService) {
    this.messageService = messageService;
    this.brewingService = brewingService;
    this.protocolService = protocolService;
    this.recipeService = recipeService;
    LOGGER.info("UserFacade created");
  }


  @Override
  public void confirmDoIodineTest(final Integer duration) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException {
    IodineTest test = new IodineTest(duration == null ? 0 : duration.intValue());
    BrewingState state = new BrewingState(Type.REQUEST, State.MASHING, Position.IODINE);
    state.setData(test);
    brewingService.confirmStep(state);
  }


  @Override
  public void confirmStep(final BrewingState state) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException {
    brewingService.confirmStep(state);
  }


  @Override
  public ActuatorDetails getCurrentActuatorDetails() {
    return brewingService.getCurrentActuatorDetails();
  }


  @Override
  public BrewingProcess getCurrentBrewingProcess() {
    return brewingService.getCurrentBrewingProcess();
  }


  @Override
  public Protocol getProtocolContent(final int id) throws ProtocolNotFoundException,
      ProtocolParsingException {
    return protocolService.getProtocolContent(id);
  }


  @Override
  public List<LogSummary> getProtocolIndex() {
    return protocolService.getProtocolIndex();
  }


  @Override
  public Recipe getRecipe(final RecipeSummary summary) throws RecipeNotFoundException,
      RecipeParseException {
    return recipeService.getRecipe(summary);
  }


  @Override
  public Recipe getRecipe(final String id) throws RecipeNotFoundException, RecipeParseException {
    return recipeService.getRecipe(id);
  }


  @Override
  public String importRecipe(final Recipe recipe) throws RecipeParseException,
      RecipeSavingException {
    return recipeService.importRecipe(recipe);
  }


  @Override
  public void notify(final Message m) {
    messageService.notify(m);
  }

  @Override
  public void notify(final PushMessage m) {
    messageService.notify(m);
  }

  @Override
  public void notify(final String data, final PushType type) {
    messageService.notify(data, type);
  }

  @Override
  public List<Message> getPushMessages() throws BrewingProcessNotFoundException {
    return brewingService.getPushMessages();
  }

  @Override
  public List<RecipeSummary> selectRecipe() throws RecipeParseException {
    return recipeService.selectRecipe();
  }

  @Override
  public void startBrewing(final Recipe r) throws BrewingProcessException {
    brewingService.startBrewing(r);
  }


  @Override
  public void startBrewing(final String recipeId) throws BrewingProcessException,
      RecipeParseException {
    LOGGER.info("Get recipe with id: " + recipeId);
    Recipe recipe = recipeService.getRecipe(recipeId);
    LOGGER.info("Start brewing of recipe with id: " + recipeId);
    brewingService.startBrewing(recipe);
  }


  @Override
  public void subscribe(final String id) {
    messageService.subscribe(id);
  }


  @Override
  public void unsubscribe(final String id) {
    messageService.unsubscribe(id);
  }

  @Override
  public BrewingState getCurrentBrewingState() throws BrewingProcessNotFoundException {
    BrewingProcess process = brewingService.getCurrentBrewingProcess();
    if (process != null) {
      return process.getState();
    }
    throw new BrewingProcessNotFoundException("No BrewingProcess currently ongoing");
  }

  @Override
  public List<Message> getCurrentMessagesSince(final Long since)
      throws BrewingProcessNotFoundException {
    BrewingProcess process = brewingService.getCurrentBrewingProcess();
    if (process != null) {
      List<Message> messages = new ArrayList<Message>();
      long sinceLong = since != null ? since.longValue() : -1;
      for (Message message : process.getBrewingLog().getMessages()) {
        if (message == null) {
          LOGGER.log(Level.ERROR, "A message in the current brewing log was null. "
              + "This should never happen, as adding null messages to the log via "
              + "method \"add(Message m)\" would cause an exception. "
              + "So something went wrong here.");
          continue;
        }
        /* ignore older messages */
        if (message.getTime() >= sinceLong) {
          messages.add(message);
        }
      }
      return messages;
    }
    throw new BrewingProcessNotFoundException("No BrewingProcess currently ongoing");
  }

  @Override
  public TreeMap<Long, Float> getTemperaturesSince(final Long since)
      throws BrewingProcessNotFoundException {
    BrewingProcess process = brewingService.getCurrentBrewingProcess();
    if (process != null) {
      TreeMap<Long, Float> tempMap = new TreeMap<Long, Float>();
      List<Message> messagesSince = getCurrentMessagesSince(since);
      for (Message message : messagesSince) {
        if (message instanceof TemperatureMessage) {
          TemperatureMessage tempMessage = (TemperatureMessage) message;
          tempMap.put(tempMessage.getTime(), tempMessage.getTemperature());
        }
      }
      return tempMap;
    }
    throw new BrewingProcessNotFoundException("No BrewingProcess currently ongoing");
  }


  @Override
  public void cancelCurrentBrewingProcess() throws BrewingProcessNotFoundException {
    brewingService.cancelCurrentBrewingProcess();
  }


  @Override
  public BrewingProcessSummary getCurrentBrewingProcessSummary()
      throws BrewingProcessNotFoundException {
    return brewingService.getCurrentBrewingProcessSummary();
  }


  @Override
  public void alarm(final String text) {
    messageService.alarm(text);
  }
}
