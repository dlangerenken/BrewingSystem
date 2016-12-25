package impl;

import general.BrewingLog;
import general.BrewingProcess;
import general.BrewingProcessSummary;
import general.BrewingProcessSummary.TemperatureLevelInfo;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.HopAddition;
import general.HopCookingPlan;
import general.MaltAddition;
import general.MashingPlan;
import general.Protocol;
import general.Recipe;
import general.TemperatureLevel;
import general.Unit;
import gson.Serializer;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.INetworkService;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import junit.framework.Assert;
import messages.ConfirmationRequestMessage;
import messages.Message;
import mocks.MockTemperatureController;
import mocks.MockUserFacadeWithAutoConfirm;
import modules.BrewingConfirmModule;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.DummyBuilder;
import utilities.NetworkRequestHelper;
import utilities.UserFacadeProvider;
import categories.IntegrationTest;
import categories.LongDurationTest;
import categories.UnitTest;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;

/**
 * This class tests a whole brewing-process (from the beginning to the end)
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BrewingProcessTest {
  /**
   * The network-service which is started once in the beginning of this test-class
   */
  private static INetworkService networkService;

  private static UserFacadeProvider provider;

  /**
   * Method which is called after executing all tests and which just shutdowns the server
   */
  @AfterClass
  public static void afterClass() {
    networkService.shutdownServer();
    networkService = null;
  }

  /**
   * Method which is called once for the complete test-series
   * 
   * @throws Exception if the injection fails this exception is thrown
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    Application.setInjector(Guice.createInjector(Modules.override(Application.DEFAULT_MODULE).with(
        new BrewingConfirmModule())));
    /*
     * needs to be called and started as the mocked push-service will answer requests
     */
    networkService = Application.get(INetworkService.class);
    networkService.startServer(NetworkRequestHelper.ADDRESS, NetworkRequestHelper.PORT);
    provider = Application.get(UserFacadeProvider.class);
  }

  private IBrewingController controller;
  @Rule
  public ExpectedException expectedException = ExpectedException.none();



  private MockTemperatureController temperatureController;

  private MockUserFacadeWithAutoConfirm userFacade;

  /**
   * Creates an empty recipe with id and description. Starts a brewing process with that recipe that
   * reaches finished state. Aborts the brewing process before the end. Confirms that it is in
   * canceled state. Then confirms the cancellation an reruns the recipe. This test verifies that
   * the second run of the brewing process also finishes correctly.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void abortAndRestartBrewingProcessTest() throws BrewingProcessException,
      InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.CANCEL, State.FINISHED, Position.END));

    MashingPlan mashingPlan = new MashingPlan();
    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    Recipe recipe =
        new Recipe("1", "Rezept 1", "Bier", System.currentTimeMillis(), mashingPlan, hopCookingPlan);

    /* First run */
    controller.startBrewing(recipe);
    // give the mashing thread some time to perform its actions
    Thread.sleep(1 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());
    /* terminate */
    controller.cancelCurrentBrewingProcess();
    Thread.sleep(100);
    Assert.assertNotNull(controller.getCurrentBrewingProcess());
    state = controller.getCurrentBrewingProcess().getState();
    Assert.assertTrue("The type of the brewing state must be \"CANCEL\".",
        state.getType() == Type.CANCEL);
    controller.confirmStep(state);
    /* Second run */
    /* re subscribe */
    controller.startBrewing(recipe);
    // give the mashing thread some time to perform its actions
    Thread.sleep(1 * 1000);
    state = controller.getCurrentBrewingProcess().getState();
    /* verify that the second run works as well */
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());
  }

  /**
   * Terminates the controller and releases taken resources
   * 
   * @throws InvalidBrewingStepException
   */
  @After
  public void after() throws InvalidBrewingStepException {
    try {
      if (controller.getCurrentBrewingProcess() != null) {
        if (provider.get() instanceof MockUserFacadeWithAutoConfirm) {
          ((MockUserFacadeWithAutoConfirm) provider.get()).setShouldConfirmNothing(true);
        }
        controller.cancelCurrentBrewingProcess();
        controller.confirmStep(controller.getCurrentBrewingProcess().getState());
      }
    } catch (BrewingProcessNotFoundException e) {
      /*
       * even though this can occur, we don't need to do anything
       */
      e.printStackTrace();
    }
  }

  /**
   * Initiates the controller and userfacade as well as setting default-values
   * 
   * @throws InterruptedException
   */
  @Before
  public void before() throws InterruptedException {
    controller = Application.get(IBrewingController.class);
    userFacade = new MockUserFacadeWithAutoConfirm(controller);
    provider.setUserFacade(userFacade);
    userFacade.getBrewingStatesNotToConfirm().clear();
    userFacade.setResponseDelayMillis(0);
    userFacade.setShouldConfirmNothing(false);
    userFacade.setBrewingController(controller);
    temperatureController = (MockTemperatureController) Application.get(ITemperatureService.class);
    temperatureController.setInstantHeatUp(true);

    Thread.sleep(500);
  }

  /**
   * Verifies that the brewing process will create a list of temperatures.
   * 
   * @throws BrewingProcessException could occur but should not!
   * @throws InterruptedException
   */
  @Category(LongDurationTest.class)
  @Test
  public void brewingProcessCreatesTemperatureLogTest() throws BrewingProcessException,
      InterruptedException {
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END));
    controller.startBrewing(DummyBuilder.createValidTestRecipe());
    // give the mashing thread some time to perform its actions
    int seconds = 10;
    Thread.sleep(seconds * 1000);
    long refTime = System.currentTimeMillis() - seconds * 1000;
    TreeMap<Long, Float> temperatures = userFacade.getTemperaturesSince(refTime);
    /* verify that there is a message */
    Assert.assertEquals(false, temperatures.isEmpty());
    /* verify all messages were created after the refTime */
    for (Entry<Long, Float> temp : temperatures.entrySet()) {
      Assert.assertEquals(true, temp.getKey() >= refTime);
    }
  }

  //
  // @Test
  // public void stuff() throws BrewingProcessException, InterruptedException {
  // testBrewingStateDataIsNullAfterConfirmation();
  // after();
  // before();
  // testBrewingProcessHopCookingEndRequestIsNotShownToUser();
  // }

  /**
   * This test verifies, that the Brewing system will send MaltAdditionRequest at the time specified
   * in the recipe and not too much later. This only works if you do not count the time spend for
   * the heating up process.
   * 
   * @throws BrewingProcessException
   */
  @Category(IntegrationTest.class)
  @Test
  public void brewingProcessSendsAddingRequestsInTime() throws BrewingProcessException {
    /*
     * this specifies how many milliseconds are accepted as the difference between an expected malt
     * addition request and the real one
     */
    long milliSecondsDeltaAllowed = 1000;
    long firstMaltAdditionRequestExpected = 5 * 1000;
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END));
    IUserFacadeService userFacadeSpy = Mockito.spy(userFacade);
    provider.setUserFacade(userFacadeSpy);
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);
    recipe.setMashingPlan(mashingPlan);
    temperatureLevels.add(new TemperatureLevel(30 * 1000, 0, 55.0f));
    maltAdditions.add(new MaltAddition(2.0f, Unit.kg, "Malz 1", firstMaltAdditionRequestExpected));

    controller.startBrewing(recipe);
    /*
     * verifying that "notify" was called at least 3 times: -this first one is mashingStartRequest
     * -the second is a prenotification message for the upcoming malt addition. THIS IS NOT A
     * ConfirmationRequestMessage, but Mockito seems to ignore this fact -the third is the second
     * real ConfirmationRequestMessage and is for adding the first ingredient
     */
    Mockito.verify(userFacadeSpy,
        Mockito.timeout(firstMaltAdditionRequestExpected + milliSecondsDeltaAllowed).atLeast(3))
        .notify(Mockito.any(ConfirmationRequestMessage.class));
  }

  /**
   * Starting a brewing process and answering some requests. Then canceling the brewing process and
   * assert that the resulting state is xx3 (Type.CANCEL)
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void brewingProcessStopsAfterCanceled() throws BrewingProcessException,
      InterruptedException {
    /* do not confirm the lautering end and not the resulting canceled state */
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.REQUEST, State.LAUTERING, Position.END));
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.CANCEL, State.LAUTERING, Position.END));

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    controller.startBrewing(usedRecipe);

    Thread.sleep(10 * 1000);
    controller.cancelCurrentBrewingProcess();
    Assert.assertNotNull(controller.getCurrentBrewingProcess());
    Assert.assertEquals(Type.CANCEL, controller.getCurrentBrewingProcess().getState().getType());
  }

  /**
   * Creates an empty recipe with id and description. Starts a brewing process with that recipe that
   * finishes instantly. Confirm the finish state and restarts the brewing with the same recipe.
   * This test verifies that the second run of the brewing process also finishes correctly.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void brewTheSameRecipeTwice() throws BrewingProcessException, InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);

    MashingPlan mashingPlan = new MashingPlan();
    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    Recipe recipe =
        new Recipe("1", "Rezept 1", "Bier", System.currentTimeMillis(), mashingPlan, hopCookingPlan);

    recipeShouldTerminate(recipe);
    recipeShouldTerminate(recipe);
  }

  /**
   * Tests that the brewing process will not end (resulting in null pointer exception in this test)
   * after 1 second of hop cooking time. It should wait for 15 minutes because the hop addition
   * request is not confirmed.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void hopCookerDoesNotFinishWithUnconfirmedRequest() throws BrewingProcessException,
      InterruptedException {
    Recipe recipe = new Recipe();
    recipe.setHopCookingPlan(new HopCookingPlan());
    recipe.getHopCookingPlan().getHopAdditions().add(new HopAddition(1.0f, Unit.kg, "Hopfen 1", 0));
    recipe.getHopCookingPlan().setDuration(1000);
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.ADDING);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    controller.startBrewing(recipe);
    // give the mashing thread enough time to perform its actions
    Thread.sleep(5 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());
  }

  /**
   * Tests that a malt addition step confirmation with no data attached can not change the current
   * brewing state. This does not expect an InvalidBrewingStepException, even though one is thrown,
   * because it would terminate the test without checking for the last assertion.
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void mashingProcessDoesNotAcceptEmptyMaltAdditionConfirmationTest()
      throws BrewingProcessException {
    userFacade.setShouldConfirmNothing(true);

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    controller.startBrewing(usedRecipe);

    controller.confirmStep(new BrewingState(Type.REQUEST, State.MASHING, Position.START));

    /* wait for the brewing controller to be in the malt addition request state (252) */
    while (controller.getCurrentBrewingProcess().getState().toValue() != 252) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        System.out.println("Could not sleep.");
      }
    }
    /* This does not contain any data what was added, so it should fail */
    try {
      controller.confirmStep(new BrewingState(Type.REQUEST, State.MASHING, Position.ADDING));
    } catch (InvalidBrewingStepException e) {
      System.out.println("Brewing process already finished.");
    }
    /* The brewing state should not be changed */
    Assert.assertEquals(252, controller.getCurrentBrewingProcess().getState().toValue());
  }

  /**
   * Tests that the a duplicated confirmation will fail
   * 
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test(expected = InvalidBrewingStepException.class)
  public void mashingProcessDoesNotAcceptUnrequestedResponseTest() throws BrewingProcessException {
    userFacade.setShouldConfirmNothing(true);

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    controller.startBrewing(usedRecipe);

    controller.confirmStep(new BrewingState(Type.REQUEST, State.MASHING, Position.START));
    // the second confirmation should trigger an InvalidBrewingStepException
    controller.confirmStep(new BrewingState(Type.REQUEST, State.MASHING, Position.START));
  }

  /**
   * THIS TEST MAY PRINT ERROR LOG. This is intended, because when the brewing process is terminated
   * and the client confirms some state too late, this confirmation does not match the current state
   * type (CANCELED). Tests that the brewing process is terminated (The brewing state type will be
   * set to CANCELED) if the client fails to send a confirmation to the brewing controller within
   * the specified time set in properties. If after that a confirmation reaches the brewing
   * controller containing the correct state and position but REQUEST instead of CANCELED the
   * current brewing process will be reset to null.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void mashingProcessTerminatesAtResponseTimeoutTest() throws BrewingProcessException,
      InterruptedException {
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.REQUEST, State.LAUTERING, Position.END));
    userFacade.setResponseDelayMillis(1000);

    // set the maximal response time to 100 ms so it causes a timeout
    ((BrewingController) controller).setMaximalWaitingTimeMillis(100);

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    try {
      controller.startBrewing(usedRecipe);
    } catch (InvalidBrewingStepException e) {
      System.out.println("<<<<<>>>>>>>>> jo");
    }
    // give the mashing thread enough time to perform its actions
    Thread.sleep(2 * 1000);
    BrewingProcess currentBrewingProcess = controller.getCurrentBrewingProcess();
    Assert.assertEquals(true, currentBrewingProcess == null
        || Type.CANCEL == currentBrewingProcess.getState().getType());
  }

  /**
   * This method validates if a given recipe terminates successfully
   * 
   * @param recipe
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  private void recipeShouldTerminate(final Recipe recipe) throws BrewingProcessException,
      InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);

    /* First run */
    controller.startBrewing(recipe);
    // give the mashing thread some time to perform its actions
    Thread.sleep(1 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());
    /* confirm last step */
    controller.confirmStep(expectedResultingState);
    Assert.assertEquals(null, controller.getCurrentBrewingProcess());
  }

  /**
   * Verifies that the brewing process can be canceled, which does not set the current brewing
   * process to null. After a confirmation with the state in which it was canceled, it will be set
   * to null.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testAbortBrewingProcess() throws BrewingProcessException, InterruptedException {
    Thread.sleep(150000);
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.MASHING, Position.START);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    userFacade.getBrewingStatesNotToConfirm().add(
        new BrewingState(Type.CANCEL, State.MASHING, Position.START));
    IUserFacadeService userFacadeSpy = Mockito.spy(userFacade);
    provider.setUserFacade(userFacadeSpy);

    controller.startBrewing(new Recipe());
    Assert.assertEquals(true, controller.getCurrentBrewingProcess() != null);
    controller.cancelCurrentBrewingProcess();
    /*
     * verifying that "notify" was called at least 3 times: -this first one is mashingStartRequest
     * -the second is a prenotification message for the upcoming malt addition. THIS IS NOT A
     * ConfirmationRequestMessage, but Mockito seems to ignore this fact -the third is the second
     * real ConfirmationRequestMessage and is for adding the first ingredient
     */
    Mockito.verify(userFacadeSpy, Mockito.timeout(500).atLeast(1)).alarm(
        (Mockito.any(String.class)));
    Assert.assertEquals(true, controller.getCurrentBrewingProcess() != null);
    BrewingState lastState =
        BrewingState.fromValue(controller.getCurrentBrewingProcess().getState().toValue());
    controller.confirmStep(lastState);
    Assert.assertEquals(false, controller.getCurrentBrewingProcess() != null);
  }

  /**
   * This tests verifies that the hopCooking end request (432) is not shown to the user. It is only
   * requested by the HopCooker and then immediately confirmed by it.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testBrewingProcessHopCookingEndRequestIsNotShownToUser()
      throws BrewingProcessException, InterruptedException {
    Recipe recipe = new Recipe();
    userFacade.setShouldConfirmNothing(true);

    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    List<HopAddition> hopAdditions = new ArrayList<HopAddition>();
    hopCookingPlan.setHopAdditions(hopAdditions);

    recipe.setHopCookingPlan(hopCookingPlan);
    hopAdditions.add(new HopAddition(1.0f, Unit.kg, "Hopfen 1", 0));
    hopCookingPlan.setDuration(3 * 1000);

    controller.startBrewing(recipe);
    Thread.sleep(100);
    controller.confirmStep(new BrewingState(Type.REQUEST, State.MASHING, Position.START));
    Thread.sleep(100);
    controller.confirmStep(new BrewingState(Type.REQUEST, State.LAUTERING, Position.END));
    Thread.sleep(100);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    controller.confirmStep(new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.START));
    Thread.sleep(100);
    state = controller.getCurrentBrewingProcess().getState();
    /* this is the first and only hop addition request for this recipe */
    Assert.assertEquals(
        new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.ADDING).toValue(),
        state.toValue());
  }

  /**
   * Verifies that after the confirmation of MaltAddition the current brewing state has no data
   * object
   * 
   * @throws BrewingProcessException could occur but should not!
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testBrewingStateDataIsNullAfterConfirmation() throws BrewingProcessException,
      InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.MASHING, Position.ADDING);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    controller.startBrewing(DummyBuilder.createValidTestRecipe());
    // give the mashing thread some time to perform its actions
    Thread.sleep(2 * 1000);
    BrewingState currentBrewingState = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(true, currentBrewingState.getData() != null);

    userFacade.setShouldConfirmNothing(true);
    controller.confirmStep(currentBrewingState);
    currentBrewingState = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(false, currentBrewingState.getData() != null);
  }

  /**
   * Tests whether or not an empty recipe is valid (should be valid)
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testEmptyRecipe() throws BrewingProcessException, InterruptedException {
    Recipe recipe = new Recipe();
    recipeShouldTerminate(recipe);
  }

  /**
   * Verifies that the hop-cooking list is not empty
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testHopCookingListNotEmpty() throws BrewingProcessException, InterruptedException {
    Recipe recipe = new Recipe();
    HopCookingPlan plan = new HopCookingPlan();
    plan.getHopAdditions().add(new HopAddition(10, Unit.g, "Test", 0));
    recipe.setHopCookingPlan(plan);
    recipeShouldTerminate(recipe);
  }

  /**
   * A hop addition with negative input time should fail
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testInvalidHopCookingListFails() throws BrewingProcessException, InterruptedException {
    Recipe recipe = new Recipe();
    HopCookingPlan plan = new HopCookingPlan();
    plan.getHopAdditions().add(new HopAddition(10, Unit.g, "Test", -1337));
    recipe.setHopCookingPlan(plan);
    expectedException.expect(BrewingProcessException.class);
    recipeShouldTerminate(recipe);
  }

  /**
   * Invalid malt addition values should result in an abort of the brewing process
   */
  @Category(IntegrationTest.class)
  @Test
  public void testInvalidMashingListFails() throws BrewingProcessException, InterruptedException {
    Recipe recipe = new Recipe();
    MashingPlan plan = new MashingPlan();
    plan.getMaltAdditions().add(new MaltAddition(10, Unit.g, "Test", -1337));
    TemperatureLevel level = new TemperatureLevel(100, 0, 40);
    plan.getTemperatureLevels().add(level);
    recipe.setMashingPlan(plan);
    expectedException.expect(BrewingProcessException.class);
    recipeShouldTerminate(recipe);
  }

  /**
   * Without temperature levels a given malt addition list should fail
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Test
  public void testInvalidMashingListNotEmpty() throws BrewingProcessException, InterruptedException {
    Recipe recipe = new Recipe();
    MashingPlan plan = new MashingPlan();
    plan.getMaltAdditions().add(new MaltAddition(10, Unit.g, "Test", 0));
    recipe.setMashingPlan(plan);
    expectedException.expect(BrewingProcessException.class);
    recipeShouldTerminate(recipe);
  }

  /**
   * Verifies if the mashing-process is continued
   * 
   * @throws BrewingProcessException Should not occur as we need to have a successful brewing
   *         process
   * @throws InterruptedException should not occur
   */
  @Category(IntegrationTest.class)
  @Test
  public void testIssue201() throws BrewingProcessException, InterruptedException {
    temperatureController.setInstantHeatUp(false);
    String recipeWhichNeedsToWork =
        "{\"name\":\"test \",\"description\":\"Das da. \",\"id\":\"04.13.2015-20.13.57\",\"date\":0,\"mashingPlan\":{\"temperatureLevels\":[{\"startTime\":0,\"temperature\":0.0,\"duration\":404}],\"maltAdditions\":[{\"inputTime\":54,\"amount\":46.0,\"unit\":\"g\",\"name\":\"jfjd\"}]},\"hopCookingPlan\":{\"hopAdditions\":[{\"inputTime\":5104,\"amount\":54.0,\"unit\":\"g\",\"name\":\"irf\"}],\"duration\":10000}}";
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    Recipe recipe = Serializer.getInstance().fromJson(recipeWhichNeedsToWork, Recipe.class);

    expectedException.expect(BrewingProcessException.class);
    /*
     * First run
     */
    controller.startBrewing(recipe);
    /*
     * give the mashing thread some time to perform its actions
     */
    Thread.sleep(20 * 1000);
  }

  /**
   * Verifies that no concurrent-modification-exception is thrown
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testSerializationDuringBrewingProcessIssue192() throws BrewingProcessException,
      InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    Thread networkThread = new Thread() {
      @Override
      public void run() {
        try {
          /*
           * give the brewing controller a bit of time ahead to start
           */
          sleep(200);
          while (true) {
            sleep(100);
            /*
             * This serialization has thrown an exception so we call it every 100ms here to check
             * that no exception occurs anymore
             */
            Serializer.getInstance().toJson(controller.getCurrentBrewingProcess());
            Serializer.getInstance().toJson(controller.getCurrentBrewingProcessSummary());
          }
        } catch (Exception e) {
          /*
           * Ignore as it is not needed
           */
          e.printStackTrace();
        }
      };
    };
    networkThread.start();
    MockTemperatureController temperatureController =
        (MockTemperatureController) Application.get(ITemperatureService.class);
    temperatureController.setInstantHeatUp(true);
    controller.startBrewing(DummyBuilder.createValidTestRecipe());
    // give the mashing thread enough time to perform its actions
    Thread.sleep(20 * 1000);
    networkThread.interrupt();
  }

  /**
   * Big test which verifies a whole brewing process
   * 
   * @throws BrewingProcessException could occur but should not!
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testWholeBrewingProcess() throws BrewingProcessException, InterruptedException {
    brewingUntilFinishedRequest();
  }

  /**
   * Verifies that the brewing will be completed, if there is not mashing or hop cooking part in the
   * recipe, so the user facade only confirms the two manual steps.
   * 
   * @throws BrewingProcessException could occur but should not!
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testWholeBrewingProcessWithEmptyRecipe() throws BrewingProcessException,
      InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    controller.startBrewing(new Recipe());
    // give the mashing thread some time to perform its actions
    Thread.sleep(1 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());
  }

  /**
   * Tests if the brewing process summary contains information about the current temperature level
   * during mashing
   */
  @Category(IntegrationTest.class)
  @Test
  public void testBrewingProcessSummaryContainsCurrentTemperatureLevel216()
      throws BrewingProcessException, InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.MASHING, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    Recipe recipe = DummyBuilder.createValidTestRecipe();
    controller.startBrewing(recipe);
    // give the mashing thread enough time to perform its actions
    Thread.sleep(5 * 1000);
    BrewingProcessSummary summary = controller.getCurrentBrewingProcessSummary();

    List<TemperatureLevelInfo> temperatureLevelInfoList = summary.getTemperatureLevelInfo();
    // temperature level exists
    Assert.assertTrue("The temperatureLevelInfoList should not be empty.",
        temperatureLevelInfoList != null && !temperatureLevelInfoList.isEmpty());

    /* verify that the last/current info matches the one in the recipe */
    TemperatureLevelInfo temperatureLevelInfo =
        temperatureLevelInfoList.get(temperatureLevelInfoList.size() - 1);
    TemperatureLevel temperatureLevel =
        recipe.getMashingPlan().getTemperatureLevels().get(temperatureLevelInfo.position - 1);
    Assert.assertEquals(temperatureLevel.getStartTime(), temperatureLevelInfo.recipeStartTime);
    Assert.assertEquals(temperatureLevel.getDuration(), temperatureLevelInfo.duration);
    Assert.assertEquals(temperatureLevel.getTemperature(), temperatureLevelInfo.temperature);

    /* verify that for all other levels there is a heatup phase */
    for (int i = 0; i < temperatureLevelInfoList.size() - 1; i += 2) {
      TemperatureLevelInfo heatUp = temperatureLevelInfoList.get(i);
      TemperatureLevelInfo level = temperatureLevelInfoList.get(i + 1);
      Assert.assertEquals(heatUp.position, level.position);
      Assert.assertTrue("Heatup start time must be less than the level start time.",
          heatUp.startTimeMillis < level.startTimeMillis);
    }
  }

  /**
   * Tests if the brewing process summary contains information about the current temperature level
   * at hop cooking
   */
  @Category(IntegrationTest.class)
  @Test
  public void testBrewingProcessSummaryContainsCurrentTemperatureLevelAtHopCooking219()
      throws BrewingProcessException, InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.ADDING);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    Recipe recipe = DummyBuilder.createValidTestRecipe();
    recipe.getMashingPlan().getMaltAdditions().clear();
    recipe.getMashingPlan().getTemperatureLevels().clear();
    controller.startBrewing(recipe);
    // give the mashing thread enough time to perform its actions
    Thread.sleep(2 * 1000);

    List<TemperatureLevelInfo> temperatureLevelInfoList =
        controller.getCurrentBrewingProcessSummary().getTemperatureLevelInfo();
    // temperature level exists
    Assert.assertTrue("The temperatureLevelInfoList should not be empty.",
        temperatureLevelInfoList != null && !temperatureLevelInfoList.isEmpty());

    TemperatureLevelInfo temperatureLevelInfo =
        temperatureLevelInfoList.get(temperatureLevelInfoList.size() - 1);
    Assert.assertEquals(1, temperatureLevelInfo.position);
    Assert.assertEquals(0, temperatureLevelInfo.recipeStartTime);
    Assert.assertEquals(recipe.getHopCookingPlan().getDuration(), temperatureLevelInfo.duration);
    Assert.assertEquals(100.0f, temperatureLevelInfo.temperature);
  }

  /**
   * Big test which verifies a whole brewing process
   * 
   * @throws BrewingProcessException could occur but should not!
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testTemperatureLevelInformationOnlyForCurrentBrewingState252()
      throws BrewingProcessException, InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.HOP_COOKING, Position.ADDING);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    controller.startBrewing(DummyBuilder.createValidTestRecipe());
    // give the mashing thread enough time to perform its actions
    Thread.sleep(20 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());

    BrewingProcessSummary summary = controller.getCurrentBrewingProcessSummary();
    List<TemperatureLevelInfo> temperatureLevels = summary.getTemperatureLevelInfo();

    /* there is no null value in the list of temperature levels */
    Assert
        .assertFalse("Temperature levels may not contain null.", temperatureLevels.contains(null));
    /*
     * there are only 2 temperature levels at the point of time of the first hop addition (the
     * heatup for hop cooking and the hop cooking level itself, no mashing levels)
     */
    Assert.assertTrue("temperatureLevels.size must be less or equal 2.",
        temperatureLevels.size() <= 2);
  }

  /**
   * Tests that the State 632 (Finished-End) is only logged once
   * 
   * @throws BrewingProcessException could occur but should not!
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testBrewingFinishLoggedOnlyOnce277() throws BrewingProcessException,
      InterruptedException {
    brewingUntilFinishedRequest();
    IBrewingLogService logger = Application.get(IBrewingLogService.class);
    Collection<Message> messages = logger.getMessages();
    int finishedMessages = 0;
    for (Message message : messages) {
      if (message instanceof ConfirmationRequestMessage) {
        ConfirmationRequestMessage confirmationRequestMessage =
            (ConfirmationRequestMessage) message;
        if (confirmationRequestMessage.getBrewingStep().toValue() > 600) {
          System.out.println("State: " + confirmationRequestMessage.getBrewingStep().toValue());
          finishedMessages++;
        }
      }
    }
    Assert.assertTrue("There may only be one finished message.", finishedMessages <= 1);
  }

  /**
   * After a successfull brewing process the completion time in the protocol is not -1.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testCompletionTimeNotNegativeAfterBrewingComplete275()
      throws BrewingProcessException, InterruptedException {
    brewingUntilFinishedRequest();

    BrewingLog log = controller.getCurrentBrewingProcess().getBrewingLog();
    controller.confirmStep(controller.getCurrentBrewingProcess().getState());
    Protocol protocol = log.getProtocol();
    Assert.assertTrue("CompletionTime is -1.", protocol.getCompletionTime() != -1);
  }

  /**
   * After a canceled brewing process the abortion time in the protocol is not -1.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void testAbortionTimeNotNegativeAfterBrewingAbort275() throws BrewingProcessException,
      InterruptedException {
    brewingUntilFinishedRequest();

    BrewingLog log = controller.getCurrentBrewingProcess().getBrewingLog();
    controller.cancelCurrentBrewingProcess();
    Protocol protocol = log.getProtocol();
    Assert.assertTrue("AbortionTime is -1.", protocol.getAbortionTime() != -1);
  }

  /**
   * Tests that after the last malt addition there is not immediately an iodine request if the
   * temperature level is not yet finished.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void iodineTestNotTooEarly() throws BrewingProcessException, InterruptedException {
    temperatureController.setInstantHeatUp(true);
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.MASHING, Position.ADDING);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);

    Recipe r = new Recipe();
    r.setMashingPlan(new MashingPlan());
    r.setHopCookingPlan(new HopCookingPlan());
    r.getMashingPlan().getMaltAdditions().add(new MaltAddition(1.0f, Unit.kg, "Malz 1", 1000));
    r.getMashingPlan().getTemperatureLevels().add(new TemperatureLevel(10 * 1000, 0, 50.0f));
    controller.startBrewing(r);
    Thread.sleep(2 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());

    userFacade.setShouldConfirmNothing(true);

    IUserFacadeService userFacadeSpy = Mockito.spy(userFacade);
    provider.setUserFacade(userFacadeSpy);

    controller.confirmStep(state);
    Mockito.verify(userFacadeSpy, Mockito.timeout(5 * 1000).times(0)).notify(
        Mockito.any(ConfirmationRequestMessage.class));
  }

  private void brewingUntilFinishedRequest() throws BrewingProcessException, InterruptedException {
    BrewingState expectedResultingState =
        new BrewingState(Type.REQUEST, State.FINISHED, Position.END);
    userFacade.getBrewingStatesNotToConfirm().add(expectedResultingState);
    controller.startBrewing(DummyBuilder.createValidTestRecipe());
    // give the mashing thread enough time to perform its actions
    Thread.sleep(20 * 1000);
    BrewingState state = controller.getCurrentBrewingProcess().getState();
    Assert.assertEquals(expectedResultingState.toValue(), state.toValue());
  }
}
