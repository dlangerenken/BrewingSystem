package network;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utilities.NetworkRequestHelper.sendGet;
import static utilities.NetworkRequestHelper.sendPost;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.DummyBuilder;
import utilities.NetworkResult;
import categories.UnitTest;
import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.InvalidBrewingStepException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import general.BrewingState;
import gson.Serializer;

/**
 * This class provides tests for the brewing resource and checks that the network-request is
 * reassigned correctly
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BrewingResourceTest extends ResourceControllerTestHelper {

  /**
   * Checks if /brewing/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewing() throws Exception {
    sendGet(serverAddress + "brewing/");
    verify(userFacade, timeout(300).times(1)).getCurrentBrewingProcess();
  }
  
  /**
   * Checks if brewing/cancel is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingCancel() throws Exception {
    sendPost(serverAddress + "brewing/cancel", "");
    verify(userFacade, timeout(300).times(1)).cancelCurrentBrewingProcess();
  }

  /**
   * Checks if an exception while canceling is successfully sent to the client
   * 
   * @throws Exception exception which can always occur
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingCancelFail() throws Exception {
    Mockito.doThrow(BrewingProcessNotFoundException.class).when(userFacade)
        .cancelCurrentBrewingProcess();

    NetworkResult result = sendPost(serverAddress + "brewing/cancel", "");
    verify(userFacade, timeout(300).times(1)).cancelCurrentBrewingProcess();
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if brewing/confirm?state=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingConfirm() throws Exception {
    BrewingState state = DummyBuilder.getBrewingState();
    sendPost(serverAddress + "brewing/confirm", "state=" + Serializer.getInstance().toJson(state));
    verify(userFacade, timeout(300).times(1)).confirmStep(state);
  }


  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingConfirmFailBrewingProcessNotFound() throws Exception {
    BrewingState state = DummyBuilder.getBrewingState();
    Mockito.doThrow(BrewingProcessNotFoundException.class).when(userFacade).confirmStep(state);
    NetworkResult result =
        sendPost(serverAddress + "brewing/confirm",
            "state=" + Serializer.getInstance().toJson(state));
    verify(userFacade, timeout(300).times(1)).confirmStep(state);
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingConfirmFailInvalidBrewingStep() throws Exception {
    BrewingState state = DummyBuilder.getBrewingState();
    Mockito.doThrow(InvalidBrewingStepException.class).when(userFacade).confirmStep(state);
    NetworkResult result =
        sendPost(serverAddress + "brewing/confirm",
            "state=" + Serializer.getInstance().toJson(state));
    verify(userFacade, timeout(300).times(1)).confirmStep(state);
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if brewing/iodine?duration=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingConfirmIodine() throws Exception {
    sendPost(serverAddress + "brewing/iodine", "duration=0");
    verify(userFacade, timeout(300).times(1)).confirmDoIodineTest(0);
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingConfirmIodineFailBrewingProcessNotFound() throws Exception {
    Mockito.doThrow(BrewingProcessNotFoundException.class).when(userFacade).confirmDoIodineTest(0);
    NetworkResult result = sendPost(serverAddress + "brewing/iodine", "duration=0");
    verify(userFacade, timeout(300).times(1)).confirmDoIodineTest(0);
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingConfirmIodineFailInvalidBrewingStep() throws Exception {
    Mockito.doThrow(InvalidBrewingStepException.class).when(userFacade).confirmDoIodineTest(0);
    NetworkResult result = sendPost(serverAddress + "brewing/iodine", "duration=0");
    verify(userFacade, timeout(300).times(1)).confirmDoIodineTest(0);
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if /brewing/ throws an exception is no brewing process is ongoing
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingFailNotFoundException() throws Exception {
    when(userFacade.getCurrentBrewingProcess()).thenReturn(null);
    NetworkResult result = sendGet(serverAddress + "brewing/");
    verify(userFacade, timeout(300).times(1)).getCurrentBrewingProcess();
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if brewing/start?recipeId=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStart() throws Exception {
    sendPost(serverAddress + "brewing/start", "recipeId=0");
    verify(userFacade, timeout(300).times(1)).startBrewing("0");
  }

  /**
   * Checks if an appropriate exception is thrown when starting a brewing process failed
   * 
   * @throws Exception exception which can always occur
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStartFailBrewingProcessException() throws Exception {
    Mockito.doThrow(BrewingProcessException.class).when(userFacade).startBrewing("0");

    NetworkResult result = sendPost(serverAddress + "brewing/start", "recipeId=0");
    verify(userFacade, timeout(300).times(1)).startBrewing("0");
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if an appropriate exception is thrown when starting a brewing process failed
   * 
   * @throws Exception exception which can always occur
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStartFailRecipeNotFoundException() throws Exception {
    Mockito.doThrow(RecipeNotFoundException.class).when(userFacade).startBrewing("0");

    NetworkResult result = sendPost(serverAddress + "brewing/start", "recipeId=0");
    verify(userFacade, timeout(300).times(1)).startBrewing("0");
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if an appropriate exception is thrown when starting a brewing process failed
   * 
   * @throws Exception exception which can always occur
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStartFailRecipeParseException() throws Exception {
    Mockito.doThrow(RecipeParseException.class).when(userFacade).startBrewing("0");

    NetworkResult result = sendPost(serverAddress + "brewing/start", "recipeId=0");
    verify(userFacade, timeout(300).times(1)).startBrewing("0");
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if brewing/state/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingState() throws Exception {
    sendGet(serverAddress + "brewing/state");
    verify(userFacade, timeout(300).times(1)).getCurrentBrewingState();
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingStateFailBrewingProcessNotFound() throws Exception {
    Mockito.doThrow(BrewingProcessNotFoundException.class).when(userFacade)
        .getCurrentBrewingState();
    NetworkResult result = sendGet(serverAddress + "brewing/state");
    verify(userFacade, timeout(300).times(1)).getCurrentBrewingState();
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if brewing/summary/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingSummary() throws Exception {
    sendGet(serverAddress + "brewing/summary");
    verify(userFacade, timeout(300).times(1)).getCurrentBrewingProcessSummary();
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingSummaryFailBrewingProcessNotFound() throws Exception {
    Mockito.doThrow(BrewingProcessNotFoundException.class).when(userFacade)
        .getCurrentBrewingProcessSummary();
    NetworkResult result = sendGet(serverAddress + "brewing/summary");
    verify(userFacade, timeout(300).times(1)).getCurrentBrewingProcessSummary();
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * This test verifies if a more complex brewing state can be sent (e.g. more than one element in
   * data) brewing/confirm?state=gson(moreComplexState)
   * 
   * @throws Exception HttpRequest-Exception which can occur
   */
  @Category(UnitTest.class)
  @Test
  public void testComplexBrewingState() throws Exception {
    /*
     * Confirm Mashing Ingredient addition
     */
    BrewingState state = BrewingState.fromValue(252);
    state.setData(DummyBuilder.getMaltAdditions());
    String gsonState = Serializer.getInstance().toJson(state);
    sendPost(serverAddress + "brewing/confirm", "state=" + gsonState);
    verify(userFacade, timeout(300).times(1)).confirmStep(state);
  }

  /**
   * This test verifies if a more complex brewing state can be sent (e.g. more than one element in
   * data)
   * 
   * @throws Exception HttpRequest-Exception which can occur
   */
  @Category(UnitTest.class)
  @Test
  public void testComplexBrewingStateWithInvalidData() throws Exception {
    /*
     * Confirm Mashing Ingredient addition, but add hop additions
     */
    BrewingState state = BrewingState.fromValue(252);
    state.setData(DummyBuilder.getHopAdditions());
    String gsonState = Serializer.getInstance().toJson(state);
    sendPost(serverAddress + "brewing/confirm", "state=" + gsonState);
    verify(userFacade, timeout(300).times(0)).confirmStep(state);
  }

  /**
   * BrewingState is ignored when sending to network-controller (posted by Tobias, see #156)
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testIssue156() throws Exception {
    BrewingState state = BrewingState.fromValue(212);
    state.setData(null);
    String gson = Serializer.getInstance().toJson(state);
    sendPost(serverAddress + "brewing/confirm", "state=" + gson);
    verify(userFacade, timeout(300)).confirmStep(state);
  }

  /**
   * The network communication now keeps the exception messages given by the caller-class and
   * returns them instead of creating their own strings
   */
  @Category(UnitTest.class)
  @Test
  public void testIssue171() {
    String testString = "This is a test string to demonstrate that the message is kept";
    BrewingProcessException exception = new BrewingProcessException(testString);
    try {
      Mockito.doThrow(exception).when(userFacade).startBrewing(Mockito.anyString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    NetworkResult result = null;
    try {
      result = sendPost(serverAddress + "brewing/start", "recipeId=0");
    } catch (Exception e) {
      e.printStackTrace();
    }
    Assert.assertTrue(result != null && result.getResult().contains(testString));
  }
  
  /**
   * Checks if /recipes/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testPushMessages() throws Exception {
    sendGet(serverAddress + "brewing/push");
    verify(userFacade, timeout(300).times(1)).getPushMessages();
  }

}
