/*
 *
 */
package impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import general.BrewingLog;
import general.BrewingProcess;
import general.BrewingState;
import general.Recipe;
import general.RecipeSummary;
import interfaces.IBrewingController;
import interfaces.IMessageService;
import interfaces.IProtocolService;
import interfaces.IRecipeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import messages.Message;
import messages.TemperatureMessage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import push.PushMessage;
import push.PushType;
import utilities.DummyBuilder;
import categories.UnitTest;
import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;


/**
 * This class tests the redirecting of the user-facade (in case something changes inside of the
 * user-facade.
 *
 * @author Daniel Langerenken
 */
@RunWith(MockitoJUnitRunner.class)
public class UserFacadeTest {

  /** The brewing service. */
  private IBrewingController brewingController;

  /**
   * Exception which should throw in some tests
   */
  @Rule
  public ExpectedException exception = ExpectedException.none();

  /** The message service. */
  private IMessageService messageService;

  /** The protocol service. */
  private IProtocolService protocolService;

  /** The recipe service. */
  private IRecipeService recipeService;

  /** The user facade. */
  private UserFacade userFacade;

  /**
   * Checks that the current brewing process is called at the controller
   * 
   * @throws BrewingProcessNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void checkCurrentBrewingProcess() throws BrewingProcessNotFoundException {
    BrewingProcess process = mock(BrewingProcess.class);
    when(userFacade.getCurrentBrewingProcess()).thenReturn(process);
    userFacade.getCurrentBrewingState();
    verify(brewingController, times(1)).getCurrentBrewingProcess();

    when(userFacade.getCurrentBrewingProcess()).thenReturn(null);
    exception.expect(BrewingProcessNotFoundException.class);
    userFacade.getCurrentBrewingState();
  }

  /**
   * Inits the services for every single test (to get different services)
   */
  @Before
  public void init() {
    messageService = mock(IMessageService.class);
    brewingController = mock(IBrewingController.class);
    recipeService = mock(IRecipeService.class);
    protocolService = mock(IProtocolService.class);
    userFacade = new UserFacade(messageService, brewingController, protocolService, recipeService);
  }

  /**
   * Tests the redirection of the brewing service.
   *
   * @throws BrewingProcessException the brewing process exception (should not be thrown in this
   *         test)
   * @throws RecipeParseException the recipe parse exception (should not be thrown in this test)
   */
  @Category(UnitTest.class)
  @Test
  public void testBrewingService() throws BrewingProcessException, RecipeParseException {
    Recipe r = mock(Recipe.class);

    userFacade.startBrewing(r);
    verify(brewingController).startBrewing(r);

    userFacade.getCurrentActuatorDetails();
    verify(brewingController).getCurrentActuatorDetails();

    BrewingState state = mock(BrewingState.class);
    userFacade.confirmStep(state);
    verify(brewingController, times(1)).confirmStep(state);

    userFacade.confirmDoIodineTest(1337);
    verify(brewingController, times(2)).confirmStep(Mockito.any(BrewingState.class));

    when(recipeService.getRecipe("42")).thenReturn(r);
    userFacade.startBrewing("42");
    verify(recipeService).getRecipe("42");
    verify(brewingController, times(2)).startBrewing(r);

    userFacade.getCurrentBrewingProcess();
    verify(brewingController).getCurrentBrewingProcess();

    userFacade.cancelCurrentBrewingProcess();
    verify(brewingController).cancelCurrentBrewingProcess();

    userFacade.getCurrentBrewingProcessSummary();
    verify(brewingController).getCurrentBrewingProcessSummary();

    userFacade.getPushMessages();
    verify(brewingController).getPushMessages();
  }

  /**
   * Tests the redirection of the message service.
   *
   * @throws ProtocolNotFoundException the protocol not found exception (should not be thrown in
   *         this test)
   */
  @Category(UnitTest.class)
  @Test
  public void testMessageService() throws ProtocolNotFoundException {
    Message m = mock(Message.class);
    PushMessage pm = mock(PushMessage.class);

    userFacade.notify(m);
    verify(messageService).notify(m);

    userFacade.notify(pm);
    verify(messageService).notify(pm);

    userFacade.notify("hello", PushType.ALARM);
    verify(messageService).notify("hello", PushType.ALARM);

    userFacade.subscribe("42");
    verify(messageService).subscribe("42");

    userFacade.unsubscribe("42");
    verify(messageService).unsubscribe("42");

    userFacade.alarm("42");
    verify(messageService).alarm("42");
  }

  /**
   * This method tests whether only messages with a higher timestamp than the given are returned
   *
   * @throws BrewingProcessNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void testMessagesSince() throws BrewingProcessNotFoundException {
    BrewingProcess process = mock(BrewingProcess.class);
    BrewingLog log = mock(BrewingLog.class);
    List<Message> messages = new ArrayList<>();
    when(process.getBrewingLog()).thenReturn(log);
    when(log.getMessages()).thenReturn(messages);
    when(brewingController.getCurrentBrewingProcess()).thenReturn(process);

    /*
     * in the beginning no message should be returned
     */
    Assert.assertEquals(0, userFacade.getTemperaturesSince(0l).size());
    Assert.assertEquals(0, userFacade.getTemperaturesSince(1337l).size());

    /*
     * only one temperature-message is added and when the given timestamp is higher than this
     * message, it shouldnt return any element
     */
    Message message = DummyBuilder.getMessage(6);
    messages.add(message);
    Assert.assertEquals(1, userFacade.getTemperaturesSince(0l).size());
    Assert.assertEquals(0, userFacade.getTemperaturesSince(message.getTime() + 1).size());

    Map<Long, Float> map = userFacade.getTemperaturesSince(0l);
    Assert
        .assertEquals(map.get(message.getTime()), ((TemperatureMessage) message).getTemperature());

    /*
     * another message which isnt a temperature-message is added - should be ignored
     */
    messages.add(DummyBuilder.getMessage(2));
    Assert.assertEquals(1, userFacade.getTemperaturesSince(0l).size());
    Assert.assertEquals(1, userFacade.getTemperaturesSince(message.getTime()).size());
    Assert.assertEquals(0, userFacade.getTemperaturesSince(message.getTime() + 1).size());

    /*
     * another temperature-messsage is added
     */
    Message message2 = DummyBuilder.getMessage(6);
    messages.add(message2);
    Assert.assertEquals(2, userFacade.getTemperaturesSince(0l).size());

    /*
     * if message2 is older than message1 only 1 message should be returned, otherwise both of them
     */
    Assert.assertEquals(message2.getTime() < message.getTime() ? 1 : 2, userFacade
        .getTemperaturesSince(message.getTime()).size());

    messages.clear();

    /*
     * every map-element must be younger than the given date
     */
    messages.addAll(DummyBuilder.getMessages());
    long selectedDate = new Date().getTime() - (1000 * 60 * 200);
    map = userFacade.getTemperaturesSince(selectedDate);
    for (Long date : map.keySet()) {
      Assert.assertTrue(date >= selectedDate);
    }

  }

  /**
   * Tests the redirection of the protocol service.
   *
   * @throws ProtocolNotFoundException the protocol not found exception (should not be thrown in
   *         this test)
   * @throws ProtocolParsingException
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocolService() throws ProtocolNotFoundException, ProtocolParsingException {
    userFacade.getProtocolIndex();
    verify(protocolService).getProtocolIndex();

    userFacade.getProtocolContent(0);
    verify(protocolService).getProtocolContent(0);
  }

  /**
   * Tests the redirection of the recipe service.
   *
   * @throws BrewingProcessException the brewing process exception (should not be thrown in this
   *         test)
   * @throws RecipeParseException the recipe parse exception (should not be thrown in this test)
   * @throws RecipeSavingException the recipe saving exception (should not be thrown in this test)
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeService() throws BrewingProcessException, RecipeParseException,
      RecipeSavingException {
    Recipe r = mock(Recipe.class);
    RecipeSummary rs = mock(RecipeSummary.class);

    userFacade.importRecipe(r);
    verify(recipeService).importRecipe(r);

    userFacade.selectRecipe();
    verify(recipeService).selectRecipe();

    userFacade.getRecipe("0");
    verify(recipeService).getRecipe("0");

    userFacade.getRecipe(rs);
    verify(recipeService).getRecipe(rs);
  }

}
