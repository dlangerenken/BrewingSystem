package impl;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import interfaces.IAcousticNotifier;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IStirrerService;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;
import messages.ConfirmationMessage;
import mocks.MockTemperatureController;
import modules.BrewingControllerTestModule;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import persistence.PersistenceHandler;
import utilities.DummyBuilder;
import categories.UnitTest;

import com.google.inject.Guice;
import com.google.inject.Provider;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HopAddition;
import general.HopCookingPlan;
import general.MaltAddition;
import general.MashingPlan;
import general.Recipe;
import general.TemperatureLevel;
import general.Unit;

/**
 * 
 * @author Patrick
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class BrewingControllerTest {

  /**
   * Sets the brewing-controller-test-module before all tests are executed
   * 
   * @throws Exception
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    Application.setInjector(Guice.createInjector(new BrewingControllerTestModule()));
  }


  /**
   * Creates a brewing controller with mockup interfaces as input parameters to perform tests on it.
   * 
   * @return
   */
  public static IBrewingController createBrewingController(
      final ITemperatureService temperatureService) {
    return Application.get(IBrewingController.class);
  }

  Provider<IUserFacadeService> provider = new Provider<IUserFacadeService>() {

    @Override
    public IUserFacadeService get() {
      return Mockito.mock(IUserFacadeService.class);
    }
  };

  /**
   * Creates a brewing controller with mockup interfaces as input parameters to perform tests on it.
   * 
   * @return
   */
  private BrewingController createBrewingControllerWithMockInput() {
    /*
     * Mockups
     */
    ITemperatureService temperatureServiceMockup = new MockTemperatureController();
    IBrewingLogService brewingLogServiceMockup = mock(IBrewingLogService.class);
    IStirrerService stirrerServiceMockup = mock(IStirrerService.class);
    BrewingPart hopCooker = mock(BrewingPart.class);
    BrewingPart masher = mock(BrewingPart.class);
    IAcousticNotifier acousticNotifier = mock(IAcousticNotifier.class);
    PersistenceHandler persistenceHandler = mock(PersistenceHandler.class);

    BrewingController brewingController =
        new BrewingController(temperatureServiceMockup, brewingLogServiceMockup,
            stirrerServiceMockup, masher, hopCooker,
            new TemperatureLogger(brewingLogServiceMockup), acousticNotifier, provider,
            persistenceHandler);
    return brewingController;
  }

  /**
   * Tests if for every incoming brewing state confirmation on a running brewing process whether it
   * was successfully confirmed or not a log entry with a ConfirmationMessage is created.
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void logIncommingConfirmationsOnRunningBrewingControllerTest()
      throws BrewingProcessException {
    ITemperatureService temperatureServiceMockup = new MockTemperatureController();
    IBrewingLogService brewingLogServiceMockup = mock(IBrewingLogService.class);
    IStirrerService stirrerServiceMockup = mock(IStirrerService.class);
    BrewingPart hopCooker = mock(BrewingPart.class);
    BrewingPart masher = mock(BrewingPart.class);
    IAcousticNotifier acousticNotifier = mock(IAcousticNotifier.class);
    PersistenceHandler persistenceHandler = mock(PersistenceHandler.class);

    BrewingController brewingController =
        new BrewingController(temperatureServiceMockup, brewingLogServiceMockup,
            stirrerServiceMockup, masher, hopCooker,
            new TemperatureLogger(brewingLogServiceMockup), acousticNotifier, provider,
            persistenceHandler);
    brewingController.startBrewing(DummyBuilder.createValidTestRecipe());

    BrewingState state = new BrewingState(Type.NORMAL, State.FINISHED, Position.ADDING); // this is
                                                                                         // arbitrary
    for (Type t : Type.values()) {
      for (State s : State.values()) {
        for (Position p : Position.values()) {
          state.setType(t);
          state.setState(s);
          state.setPosition(p);

          try {
            brewingController.confirmStep(state);
          } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
            // maybe the confirmation fails, but here we only care if the try is logged
          }
          verify(brewingLogServiceMockup, atLeast(1)).log(Mockito.any(ConfirmationMessage.class));
        }
      }
    }
  }

  /**
   * Tests that in the initial state the only valid confirmation is mashing start
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void performBrewingStateConfirmationTest() throws BrewingProcessException {
    BrewingController brewingController = createBrewingControllerWithMockInput();
    brewingController.startBrewing(DummyBuilder.createValidTestRecipe());
    verifyEverythingFailsBut(Type.REQUEST, State.MASHING, Position.START, brewingController);
  }

  /**
   * Tests that a recipe without malt additions is invalid.
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test(expected = BrewingProcessException.class)
  public void performIncompleteRecipeValidationTest() throws BrewingProcessException {
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();

    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    List<HopAddition> hopAdditions = new ArrayList<HopAddition>();
    hopCookingPlan.setHopAdditions(hopAdditions);

    recipe.setMashingPlan(mashingPlan);
    recipe.setHopCookingPlan(hopCookingPlan);

    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(1 * 1000, 0, 50.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(1 * 1000, 1 * 1000, 60.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(2 * 1000, 2 * 1000, 75.0f);
    temperatureLevels.add(tempLevel);

    HopAddition ha = new HopAddition(1.0f, Unit.kg, "Hopfen 1", 2 * 1000);
    hopAdditions.add(ha);

    ha = new HopAddition(2.0f, Unit.kg, "Hopfen Zwo", 3 * 1000);
    hopAdditions.add(ha);

    ha = new HopAddition(1.5f, Unit.kg, "Hopfen 3", 6 * 1000);
    hopAdditions.add(ha);

    BrewingController brewingController = createBrewingControllerWithMockInput();
    brewingController.startBrewing(recipe);
  }

  /**
   * Tests that a valid recipe is accepted correctly
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void performLoadValidRecipeTest() throws BrewingProcessException {
    BrewingController brewingController = createBrewingControllerWithMockInput();
    brewingController.startBrewing(DummyBuilder.createValidTestRecipe());
  }

  /**
   * Tests that no confirmation can be sent to the controller successfully if it is not started.
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void performNoConfirmationOnUnstartedBrewingProcessTest() throws BrewingProcessException {
    BrewingController brewingController = createBrewingControllerWithMockInput();
    verifyAllFail(brewingController); // the brewing process was not started, so everything fails
  }

  /**
   * This checks all possible brewing states against the brewing controllers confirmStep method and
   * checks if (all fail)/(all but the specified fail) and throws an exception if it is not
   * successfull
   * 
   * @param type
   * @param state
   * @param position
   * @param brewingController
   * @param lastSucceeds
   * @throws BrewingProcessException
   */
  private void testAllBrewingStates(final Type type, final State state, final Position position,
      final BrewingController brewingController, final boolean lastSucceeds)
      throws BrewingProcessException {
    BrewingState failState = new BrewingState(Type.NORMAL, State.FINISHED, Position.ADDING); // this
                                                                                             // is
                                                                                             // arbitrary
    for (Type t : Type.values()) {
      for (State s : State.values()) {
        for (Position p : Position.values()) {
          failState.setType(t);
          failState.setState(s);
          failState.setPosition(p);

          if (lastSucceeds && type == t && state == s && position == p) {
            continue; // skip this for now
          }
          try {
            brewingController.confirmStep(failState);
            throw new BrewingProcessException(failState + " passed confirmStep illegally.");
          } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
          }
        }
      }
    }
    if (lastSucceeds) {
      failState.setType(type);
      failState.setState(state);
      failState.setPosition(position);
      try {
        brewingController.confirmStep(failState);
      } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
        throw new BrewingProcessException(failState + " did not pass confirmStep.");
      }
    }
  }

  /**
   * Verifies that at the given time no brewing state can be confirmed successfully
   * 
   * @param brewingController
   * @throws BrewingProcessException
   */
  private void verifyAllFail(final BrewingController brewingController) {
    try {
      testAllBrewingStates(null, null, null, brewingController, false);
    } catch (BrewingProcessException e) {
      throw new RuntimeException("Not all states failed: " + e.getMessage());
    }
  }

  /**
   * Tests the full chain of confirmations sent to the brewing controller and the resulting states
   * by only interactions with the UserFacade. I.e. inside the mashing process the position during
   * the mashing state is not set to ONGOING
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void verifyBrewingProcessChainTest() throws BrewingProcessException {
    BrewingController brewingController = createBrewingControllerWithMockInput();
    brewingController.startBrewing(DummyBuilder.createValidTestRecipe());

    /*
     * List of all brewing states the user has to confirm. Maybe occurring double states are only
     * represented once.
     */
    List<BrewingState> expectedBrewingStates = new LinkedList<BrewingState>();
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.MASHING, Position.START));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.MASHING, Position.ADDING));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.MASHING, Position.IODINE));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.LAUTERING, Position.END));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.START));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.ADDING));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.WHIRLPOOL, Position.END));
    expectedBrewingStates.add(new BrewingState(Type.REQUEST, State.FINISHED, Position.END));

    Iterator<BrewingState> brewingStateIterator = expectedBrewingStates.iterator();
    BrewingState currentBrewingState =
        brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;
    // Type.REQUEST, State.MASHING, Position.START
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    // request malt addition manually
    brewingController.setRequestState(Position.ADDING, Type.REQUEST, null);

    // Type.REQUEST, State.MASHING, Position.ADDING
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    // request iodine addition manually
    brewingController.setRequestState(Position.IODINE, Type.REQUEST, null);

    // Type.REQUEST, State.MASHING, Position.IODINE
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);

    // request end of mashing manually
    brewingController.setRequestState(Position.END, Type.INTERN, null);
    currentBrewingState.setPosition(Position.END); // this is done by the masher
    currentBrewingState.setType(Type.INTERN);
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState.setType(Type.REQUEST);

    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    /*
     * This is necessary to give the brewing controller some time to remove itself as an observer of
     * the mashing process.
     */
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }

    // Type.REQUEST, State.LAUTERING, Position.END
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    // Type.REQUEST, State.HOPCOOKING, Position.START
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    // request hop addition manually
    brewingController.setRequestState(Position.ADDING, Type.REQUEST, null);

    // Type.REQUEST, State.HOPCOOKING, Position.ADDING
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);

    // request end of hop cooking manually
    brewingController.setRequestState(Position.END, Type.INTERN, null);
    currentBrewingState.setPosition(Position.END); // this is done by the hop cooker
    currentBrewingState.setType(Type.INTERN); // this is done by the hop cooker
    brewingController.confirmStep(currentBrewingState);

    /*
     * This is necessary to give the brewing controller some time to remove itself as an observer of
     * the hop cooking process.
     */
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
    }

    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    // Type.REQUEST, State.WHIRLPOOL, Position.END
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    // Type.REQUEST, State.FINISHED, Position.END
    Assert.assertEquals(currentBrewingState.toValue(), brewingController.getCurrentBrewingProcess()
        .getState().toValue());
    brewingController.confirmStep(currentBrewingState);
    currentBrewingState = brewingStateIterator.hasNext() ? brewingStateIterator.next() : null;

    Assert.assertEquals(currentBrewingState == null, true);
  }

  /**
   * Verifies that all other brewing states fail and the last one succeeds
   * 
   * @param type
   * @param state
   * @param position
   * @param brewingController
   * @throws BrewingProcessException
   */
  private void verifyEverythingFailsBut(final Type type, final State state,
      final Position position, final BrewingController brewingController) {
    try {
      testAllBrewingStates(type, state, position, brewingController, true);
    } catch (BrewingProcessException e) {
      throw new RuntimeException("Testing state " + position + "," + state + "," + type + ": "
          + e.getMessage());
    }
  }
}
