package impl;

import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import general.BrewingState;
import general.BrewingState.Position;
import general.BrewingState.State;
import general.BrewingState.Type;
import general.IodineTest;
import general.MaltAddition;
import general.MashingPlan;
import general.Recipe;
import general.TemperatureLevel;
import general.Unit;
import interfaces.IBrewingController;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;
import messages.ConfirmationRequestMessage;
import messages.Message;
import mocks.MockTemperatureController;
import mocks.MockUserFacadeWithAutoConfirm;
import modules.BrewingControllerTestModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import utilities.DummyBuilder;
import utilities.UserFacadeProvider;
import categories.IntegrationTest;
import categories.UnitTest;

import com.google.inject.Guice;

/**
 * Tests the masher
 * 
 * @author Patrick
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MasherTest {
  UserFacadeProvider userFacadeProvider;

  @Before
  public void beforeClass() throws Exception {
    Application.setInjector(Guice.createInjector(new BrewingControllerTestModule()), true);
    userFacadeProvider = Application.get(UserFacadeProvider.class);
    userFacadeProvider.setUserFacade(Mockito.mock(IUserFacadeService.class));
  }


  /**
   * This creates a IUserFacadeService mock up which simulates a client that ignores single malt
   * addition requests and confirm 3 at a time.
   * 
   * @param brewingController
   * @return
   */
  private IUserFacadeService createUserFacadeWithDelayedMultipleAddingConfirmation(
      final IBrewingController brewingController) {
    IUserFacadeService userFacade = Mockito.mock(IUserFacadeService.class);
    Mockito.doAnswer(new Answer<Void>() {

      @Override
      public Void answer(final InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Message m = (Message) args[0];
        if (!(m instanceof ConfirmationRequestMessage)) {
          return null;
        }
        ConfirmationRequestMessage cm = (ConfirmationRequestMessage) m;
        BrewingState state = cm.getBrewingStep();
        /* 332 is lautering end request */
        if (state.toValue() == 332) {
          return null;
        }
        if (state.getPosition() == Position.IODINE) {
          // creating iodine test result
          state.setData(new IodineTest(0));
        }
        try {
          boolean waitFor3AddingRequests = false;
          if (state.getData() != null && state.getData() instanceof Collection<?>) {
            @SuppressWarnings("unchecked")
            Collection<MaltAddition> maltAddtitions = (Collection<MaltAddition>) state.getData();
            state.setData(new ArrayList<MaltAddition>(maltAddtitions));
            /* confirm only when 3 additions are requested */
            if (maltAddtitions.size() < 3) {
              waitFor3AddingRequests = true;
            }
          }
          if (!waitFor3AddingRequests) {
            brewingController.confirmStep(state);
          }
        } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
          throw new RuntimeException(e);
        }
        return null;
      }
    }).when(userFacade).notify(Mockito.any(Message.class));
    return userFacade;
  }

  /**
   * This creates a IUserFacadeService mock up which simulates a client confirms a random malt
   * addition more than requested.
   * 
   * @param brewingController
   * @return
   */
  private IUserFacadeService createUserFacadeWithTooManyAddingConfirmations(
      final IBrewingController brewingController) {
    IUserFacadeService userFacade = Mockito.mock(IUserFacadeService.class);
    Mockito.doAnswer(new Answer<Void>() {

      @Override
      public Void answer(final InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Message m = (Message) args[0];
        if (!(m instanceof ConfirmationRequestMessage)) {
          return null;
        }
        ConfirmationRequestMessage cm = (ConfirmationRequestMessage) m;
        BrewingState state = cm.getBrewingStep();
        /* 332 is lautering end request */
        if (state.toValue() == 332) {
          return null;
        }
        if (state.getPosition() == Position.IODINE) {
          // creating iodine test result
          state.setData(new IodineTest(0));
        }
        try {
          if (state.getData() != null && state.getData() instanceof Collection<?>) {
            @SuppressWarnings("unchecked")
            Collection<MaltAddition> maltAddtitions = (Collection<MaltAddition>) state.getData();
            List<MaltAddition> malAdditionsList = new ArrayList<MaltAddition>(maltAddtitions);
            malAdditionsList.add(new MaltAddition(1.0f, Unit.g, "False confirm", 0));
            state.setData(malAdditionsList);
            /* confirm only when 3 additions are requested */
          }
          brewingController.confirmStep(state);
        } catch (BrewingProcessNotFoundException | InvalidBrewingStepException e) {
          System.err.println("Test successfull:\nCaught an exception: " + e.getMessage());
        }
        return null;
      }
    }).when(userFacade).notify(Mockito.any(Message.class));
    return userFacade;
  }

  /**
   * Tests if the masher can handle a recipe that has no MashingPlan part. In that case the mashing
   * process should be skipped and the resulting brewing state should be lautering end request.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(UnitTest.class)
  @Test
  public void masherDealsWithEmptyMashingPlanTest() throws BrewingProcessException,
      InterruptedException {
    ITemperatureService temperatureService = Application.get(ITemperatureService.class);
    ((MockTemperatureController) temperatureService).setInstantHeatUp(true);

    IBrewingController brewingController =
        BrewingControllerTest.createBrewingController(temperatureService);

    IUserFacadeService userFacadeService =
        createUserFacadeWithDelayedMultipleAddingConfirmation(brewingController);
    userFacadeProvider.setUserFacade(userFacadeService);

    brewingController.startBrewing(new Recipe());
    // give the mashing thread enough time to perform its actions
    Thread.sleep(1 * 1000);
    Assert.assertEquals(
        true,
        brewingController.getCurrentBrewingProcess().getState()
            .equals(Type.REQUEST, State.LAUTERING, Position.END));
  }

  /**
   * Tests the mashing process and communication between Masher and BrewingController by using a
   * IUserFacadeService that confirms all incoming requests.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void mashingProcessTest() throws BrewingProcessException, InterruptedException {
    ITemperatureService temperatureService = Application.get(ITemperatureService.class);
    ((MockTemperatureController) temperatureService).setInstantHeatUp(true);

    IBrewingController brewingController =
        BrewingControllerTest.createBrewingController(temperatureService);

    MockUserFacadeWithAutoConfirm userFacadeService =
        new MockUserFacadeWithAutoConfirm(brewingController, new BrewingState(Type.REQUEST,
            State.LAUTERING, Position.END), 0);
    testMashingProcess(brewingController, userFacadeService);
  }

  /**
   * Tests the mashing process and communication between Masher and BrewingController by using a
   * IUserFacadeService that confirms 3 malt additions at a time.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void mashingProcessWithMultipleAdditionConfirmTest() throws BrewingProcessException,
      InterruptedException {
    ITemperatureService temperatureService = Application.get(ITemperatureService.class);
    ((MockTemperatureController) temperatureService).setInstantHeatUp(true);

    IBrewingController brewingController =
        BrewingControllerTest.createBrewingController(temperatureService);

    IUserFacadeService userFacadeService =
        createUserFacadeWithDelayedMultipleAddingConfirmation(brewingController);
    testMashingProcess(brewingController, userFacadeService);
  }

  /**
   * Verifies that the Masher immediately responds to a negative iodine test and does not block the
   * entire process for the time the user wants to wait until the next test will be performed. This
   * test creates a recipe that only contains one malt addition at the start, confirms this manually
   * without a user facade, then confirms the iodine test as negative and sets the time until the
   * next test will be performed to 10 seconds. It asserts that the time passed between sending the
   * confirmation and the return of the method is no longer than 100ms. This especially verifies,
   * that the masher is not blocked for 10s.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(UnitTest.class)
  @Test
  public void mashingProcessWithNegativeIodineTest() throws BrewingProcessException,
      InterruptedException {
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    recipe.setMashingPlan(mashingPlan);

    temperatureLevels.add(new TemperatureLevel(1 * 1000, 0, 50.0f));
    MaltAddition maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 0);
    maltAdditions.add(maltAddition);

    ITemperatureService temperatureService = Application.get(ITemperatureService.class);
    ((MockTemperatureController) temperatureService).setInstantHeatUp(true);
    IBrewingController brewingController =
        BrewingControllerTest.createBrewingController(temperatureService);
    brewingController.startBrewing(recipe);
    BrewingState state = new BrewingState(Type.REQUEST, State.MASHING, Position.START);
    brewingController.confirmStep(state);
    Thread.sleep(2000);
    state = new BrewingState(Type.REQUEST, State.MASHING, Position.ADDING);
    ArrayList<MaltAddition> data = new ArrayList<MaltAddition>(1);
    data.add(maltAddition);
    state.setData(data);
    brewingController.confirmStep(state);
    Thread.sleep(2000);
    state = new BrewingState(Type.REQUEST, State.MASHING, Position.IODINE);
    state.setData(new IodineTest(10));
    long timeBeforeConfirmation = System.currentTimeMillis();
    brewingController.confirmStep(state);
    long timePassed = System.currentTimeMillis() - timeBeforeConfirmation;
    Assert.assertEquals(true, timePassed < 100);
  }

  /**
   * THIS TEST WILL PRINT TO SYSERR. This behavior is intended and means that the test was
   * successfully. This is because it is not possible to expect exceptions thrown from in a
   * Mockito.when block. Or at least I don't know how. Tests the mashing process and communication
   * between Masher and BrewingController by using a IUserFacadeService that confirms to many steps
   * at once.
   * 
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  @Category(IntegrationTest.class)
  @Test
  public void mashingProcessWithTooManyAdditionConfirmTest() throws BrewingProcessException,
      InterruptedException {

    ITemperatureService temperatureService = Application.get(ITemperatureService.class);
    ((MockTemperatureController) temperatureService).setInstantHeatUp(true);

    IBrewingController brewingController =
        BrewingControllerTest.createBrewingController(temperatureService);
    IUserFacadeService userFacadeService =
        createUserFacadeWithTooManyAddingConfirmations(brewingController);
    userFacadeProvider.setUserFacade(userFacadeService);

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    brewingController.startBrewing(usedRecipe);

    // give the mashing thread enough time to perform its actions
    Thread.sleep(5 * 1000);
    BrewingState currentBrewingState = brewingController.getCurrentBrewingProcess().getState();
    /*
     * this will not be equal, because the mashing process does not finish appropriately because all
     * confirmations contain too many malt additions
     */
    Assert.assertEquals(false,
        currentBrewingState.equals(Type.REQUEST, State.LAUTERING, Position.END));
  }

  /**
   * Tests the mashing process and communication between Masher and BrewingController with a given
   * UserFacade mock up
   * 
   * @param brewingController
   * @param userFacadeToUse the user facade mock up instance
   * @throws BrewingProcessException
   * @throws InterruptedException
   */
  private void testMashingProcess(final IBrewingController brewingController,
      final IUserFacadeService userFacadeToUse) throws BrewingProcessException,
      InterruptedException {
    userFacadeProvider.setUserFacade(userFacadeToUse);

    Recipe usedRecipe = DummyBuilder.createValidTestRecipe();
    brewingController.startBrewing(usedRecipe);

    // give the mashing thread enough time to perform its actions
    Thread.sleep(10 * 1000);
    BrewingState currentBrewingState = brewingController.getCurrentBrewingProcess().getState();
    Assert.assertEquals(true,
        currentBrewingState.equals(Type.REQUEST, State.LAUTERING, Position.END));
    if (userFacadeToUse instanceof MockUserFacadeWithAutoConfirm) {
      validateRequests(usedRecipe,
          ((MockUserFacadeWithAutoConfirm) userFacadeToUse).getRequestedBrewingStates());
    }
  }

  /**
   * This method validates that for every malt addition listed in the recipe a confirmation request
   * has been sent.
   * 
   * @param recipe
   * @param requests
   */
  private void validateRequests(final Recipe recipe, final List<BrewingState> requests) {
    Iterator<BrewingState> iterator = requests.iterator();
    BrewingState currentState = iterator.next();
    // first request is start mashing
    Assert.assertEquals(true, currentState.equals(Type.REQUEST, State.MASHING, Position.START));
    for (@SuppressWarnings("unused")
    MaltAddition maltAddition : recipe.getMashingPlan().getMaltAdditions()) {
      // there is a request
      currentState = iterator.next();
      // the addition of a malt is requested
      Assert.assertEquals(true, currentState.equals(Type.REQUEST, State.MASHING, Position.ADDING));
    }
    // there is at least one iodine test
    currentState = iterator.next();
    Assert.assertEquals(true, currentState.equals(Type.REQUEST, State.MASHING, Position.IODINE));
    // everything after the first iodine test is also a iodine test or the end of lautering and
    // therefore the end
    while (iterator.hasNext()) {
      currentState = iterator.next();
      if (currentState.getState() == State.LAUTERING) {
        // after the lautering request there are no more requests
        Assert.assertEquals(false, iterator.hasNext());
      } else {
        Assert
            .assertEquals(true, currentState.equals(Type.REQUEST, State.MASHING, Position.IODINE));
      }
    }
  }
}
