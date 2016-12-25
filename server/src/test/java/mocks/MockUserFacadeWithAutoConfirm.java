package mocks;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import exceptions.ProtocolNotFoundException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.ActuatorDetails;
import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingState;
import general.BrewingState.Position;
import general.IodineTest;
import general.LogSummary;
import general.MaltAddition;
import general.Protocol;
import general.Recipe;
import general.RecipeSummary;
import interfaces.IBrewingController;
import interfaces.IUserFacadeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import messages.ConfirmationRequestMessage;
import messages.Message;
import messages.TemperatureMessage;
import push.PushMessage;
import push.PushType;

import com.google.inject.Singleton;

/**
 * A mocked user facade that confirms every request.
 * 
 * @author Patrick
 *
 */
@Singleton
public class MockUserFacadeWithAutoConfirm implements IUserFacadeService {

  private IBrewingController brewingController;
  private final List<BrewingState> brewingStatesNotToConfirm;
  private final List<BrewingState> requestedBrewingStates;
  private long responseDelayMillis;

  private boolean shouldConfirmNothing = false;

  /**
   * Creates an implementation of IUserFacadeService with all mocked methods except
   * "notify(Message m)" which automatically confirms all ConfirmationRequestMessages and adds every
   * confirmed state to a list for later verifying.
   * 
   * @param responseDelayMillis time to wait after a received request to respond
   */
  public MockUserFacadeWithAutoConfirm(final IBrewingController brewingController) {
    this(new ArrayList<BrewingState>(), 0);
    setBrewingController(brewingController);
  }

  /**
   * Creates an implementation of IUserFacadeService with all mocked methods except
   * "notify(Message m)" which automatically confirms all ConfirmationRequestMessages and adds every
   * confirmed state to a list for later verifying.
   * 
   * @param firstBrewingStateNotToConfirm
   * @param brewingController
   * @param responseDelayMillis time to wait after a received request to respond
   */
  public MockUserFacadeWithAutoConfirm(final IBrewingController brewingController,
      final BrewingState firstBrewingStateNotToConfirm, final long responseDelayMillis) {
    this(Arrays.asList(firstBrewingStateNotToConfirm), responseDelayMillis);
    setBrewingController(brewingController);
  }

  /**
   * Creates an implementation of IUserFacadeService with all mocked methods except
   * "notify(Message m)" which automatically confirms all ConfirmationRequestMessages and adds every
   * confirmed state to a list for later verifying.
   * 
   * @param brewingStatesNotToConfirm a list of states that are not confirmed
   * @param brewingController
   * @param responseDelayMillis time to wait after a received request to respond
   */
  public MockUserFacadeWithAutoConfirm(final List<BrewingState> brewingStatesNotToConfirm,
      final long responseDelayMillis) {
    requestedBrewingStates = new LinkedList<BrewingState>();
    this.brewingStatesNotToConfirm = brewingStatesNotToConfirm;
    this.responseDelayMillis = responseDelayMillis;
  }

  @Override
  public void alarm(final String text) {
    // should do nothing as we dont need to take care of alarms in tests
  }

  @Override
  public void cancelCurrentBrewingProcess() throws BrewingProcessNotFoundException {
    /*
     * success - do nothing
     */
  }

  @Override
  public void confirmDoIodineTest(final Integer duration) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException {}

  @Override
  public void confirmStep(final BrewingState brewingState) throws BrewingProcessNotFoundException,
      InvalidBrewingStepException {}

  public List<BrewingState> getBrewingStatesNotToConfirm() {
    return brewingStatesNotToConfirm;
  }

  @Override
  public ActuatorDetails getCurrentActuatorDetails() {
    return null;
  }

  @Override
  public BrewingProcess getCurrentBrewingProcess() {
    return null;
  }

  @Override
  public BrewingProcessSummary getCurrentBrewingProcessSummary()
      throws BrewingProcessNotFoundException {
    return null;
  }

  @Override
  public BrewingState getCurrentBrewingState() throws BrewingProcessNotFoundException {
    return null;
  }

  @Override
  public List<Message> getCurrentMessagesSince(final Long since)
      throws BrewingProcessNotFoundException {
    BrewingProcess process = brewingController.getCurrentBrewingProcess();
    if (process == null) {
      throw new BrewingProcessNotFoundException("No BrewingProcess currently ongoing");
    }
    List<Message> messages = new ArrayList<Message>();
    long sinceLong = since != null ? since.longValue() : -1;
    for (Message message : process.getBrewingLog().getMessages()) {
      /* ignore older messages */
      if (message == null) {
        System.err.println("A message in the current brewing log was null. This should never "
            + "happen, as adding null messages to the log via method "
            + "\"add(Message m)\" would cause an exception. " + "So something went wrong here.");
      } else {
        if (message.getTime() >= sinceLong) {
          messages.add(message);
        }
      }
    }
    return messages;
  }

  @Override
  public Protocol getProtocolContent(final int id) throws ProtocolNotFoundException {
    return null;
  }

  @Override
  public List<LogSummary> getProtocolIndex() {
    return null;
  }

  @Override
  public List<Message> getPushMessages() throws BrewingProcessNotFoundException {
    /*
     * returns empty list
     */
    return new ArrayList<Message>();
  }

  @Override
  public Recipe getRecipe(final RecipeSummary summary) throws RecipeNotFoundException,
      RecipeParseException {
    return null;
  }

  @Override
  public Recipe getRecipe(final String id) throws RecipeNotFoundException, RecipeParseException {
    return null;
  }

  public List<BrewingState> getRequestedBrewingStates() {
    return requestedBrewingStates;
  }

  public long getResponseDelayMillis() {
    return responseDelayMillis;
  }

  @Override
  public TreeMap<Long, Float> getTemperaturesSince(final Long since)
      throws BrewingProcessNotFoundException {
    BrewingProcess process = brewingController.getCurrentBrewingProcess();
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
  public String importRecipe(final Recipe recipe) throws RecipeParseException,
      RecipeSavingException {
    return null;
  }

  public boolean isShouldConfirmNothing() {
    return shouldConfirmNothing;
  }

  @Override
  public void notify(final Message m) {
    if (!(m instanceof ConfirmationRequestMessage) || shouldConfirmNothing) {
      return;
    }
    ConfirmationRequestMessage cm = (ConfirmationRequestMessage) m;
    BrewingState state = cm.getBrewingStep();
    requestedBrewingStates.add(new BrewingState(state.getType(), state.getState(), state
        .getPosition(), state.getData()));
    for (BrewingState notConfirmState : brewingStatesNotToConfirm) {
      if (state.toValue() == notConfirmState.toValue()) {
        return;
      }
    }
    if (state.getPosition() == Position.IODINE) {
      // creating iodine test result
      state.setData(new IodineTest(0));
    }
    try {
      if (responseDelayMillis > 0) {
        try {
          Thread.sleep(responseDelayMillis);
        } catch (InterruptedException e) {
          /* in the mock this is not logged */
        }
      }
      if (state.getData() != null && state.getData() instanceof Collection<?>) {
        @SuppressWarnings("unchecked")
        Collection<MaltAddition> maltAddtitions = (Collection<MaltAddition>) state.getData();
        state.setData(new ArrayList<MaltAddition>(maltAddtitions));
      }
      brewingController.confirmStep(state);
    } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void notify(final PushMessage m) {}

  @Override
  public void notify(final String data, final PushType type) {}

  @Override
  public List<RecipeSummary> selectRecipe() throws RecipeParseException {
    return null;
  }

  public void setBrewingController(final IBrewingController brewingController) {
    this.brewingController = brewingController;
  }

  public void setResponseDelayMillis(final long millis) {
    responseDelayMillis = millis;
  }

  public void setShouldConfirmNothing(final boolean shouldConfirmNothing) {
    this.shouldConfirmNothing = shouldConfirmNothing;
  }

  @Override
  public void startBrewing(final Recipe r) throws BrewingProcessException {}

  @Override
  public void startBrewing(final String recipeId) throws RecipeNotFoundException,
      BrewingProcessException, RecipeParseException {}

  @Override
  public void subscribe(final String id) {}

  @Override
  public void unsubscribe(final String id) {}
}
