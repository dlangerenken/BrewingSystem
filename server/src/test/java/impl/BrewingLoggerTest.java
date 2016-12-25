/*
 * 
 */
package impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import general.BrewingLog;
import general.BrewingProcess;
import general.MessagePriority;
import general.Recipe;
import interfaces.ILogStorage;
import junit.framework.Assert;

import messages.HopAdditionMessage;
import messages.Message;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;
import exceptions.BrewingProcessException;
import exceptions.BrewingProcessNotFoundException;
import exceptions.LogSavingException;


/**
 * This class tests the methods of the brewing logger.
 *
 * @author Daniel Langerenken
 */
@RunWith(MockitoJUnitRunner.class)
public class BrewingLoggerTest {

  /** The exception which should occur in a few tests */
  @Rule
  public ExpectedException exception = ExpectedException.none();

  /** BrewingLogger instance which is reinitiated every test. */
  private BrewingLogger logger;

  /**
   * Mocked log-storage for testing purposes
   */
  private ILogStorage logStorage;

  /** Mock message which only has to "exist". */
  private final Message mock = mock(Message.class);

  /**
   * Inits the logger before every single test
   */
  @Before
  public void init() {
    logStorage = mock(ILogStorage.class);
    logger = new BrewingLogger(logStorage);
  }

  /**
   * Tests if exceptions are thrown when the brewing process is null
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception which could
   *         occur
   * @throws BrewingProcessException the brewing process exception which should occur
   */
  @Category(UnitTest.class)
  @Test
  public void testExceptionWhenBrewingProcessIsNull() throws BrewingProcessNotFoundException,
      BrewingProcessException {
    exception.expect(BrewingProcessNotFoundException.class);
    logger.startLog(null);
  }

  /**
   * Tests if exceptions are thrown when not initialized.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception which can occur
   * @throws BrewingProcessException the brewing process exception which can occur
   */
  @Category(UnitTest.class)
  @Test
  public void testExceptionWhenNotInitialized() throws BrewingProcessNotFoundException,
      BrewingProcessException {
    exception.expect(BrewingProcessNotFoundException.class);
    logger.log(mock);
    logger.finishLog();
    logger.startLog(null);
    logger.getMessages();
    logger.getMessagesByPriority(MessagePriority.ALWAYS);
  }

  /**
   * Tests if exceptions are thrown when the logger is re-initialized.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception which could
   *         occur
   * @throws BrewingProcessException the brewing process exception which should occur
   */
  @Category(UnitTest.class)
  @Test
  public void testExceptionWhenReInitialized() throws BrewingProcessNotFoundException,
      BrewingProcessException {
    logger.startLog(new BrewingProcess(mock(Recipe.class), 1));
    exception.expect(BrewingProcessException.class);
    logger.startLog(new BrewingProcess(mock(Recipe.class), 2));
  }

  /**
   * Tests if an exception is thrown when a brewing process is logged which is not started.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception which should
   *         occur
   * @throws BrewingProcessException the brewing process exception which could be thrown (not in
   *         this test)
   */
  @Category(UnitTest.class)
  @Test
  public void testExceptionWhenReStarted() throws BrewingProcessNotFoundException,
      BrewingProcessException {
    logger.startLog(new BrewingProcess(mock(Recipe.class), 3));
    logger.log(mock);
    logger.log(mock);
    logger.finishLog();
    exception.expect(BrewingProcessNotFoundException.class);
    logger.log(mock);
  }

  /**
   * Tests whether or not a brewing-process not found exception is thrown if no brewing-process or
   * log exists when calling finishLog
   * 
   * @throws BrewingProcessNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void testFinishLogThrowsExceptionIfNull() throws BrewingProcessNotFoundException {
    exception.expect(BrewingProcessNotFoundException.class);
    logger.finishLog();
  }

  /**
   * Tests whether or not a brewing-process not found exception is thrown if no brewing-process or
   * log exists when calling getMessagesByPriority
   * 
   * @throws BrewingProcessNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void testGetMessagesByPriorityThrowsExceptionIfNull() throws BrewingProcessNotFoundException {
    exception.expect(BrewingProcessNotFoundException.class);
    logger.getMessagesByPriority(MessagePriority.HIGH);
  }

  /**
   * Tests whether or not a brewing-process not found exception is thrown if no brewing-process or
   * log exists when calling getMessages
   * 
   * @throws BrewingProcessNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void testGetMessagesThrowsExceptionIfNull() throws BrewingProcessNotFoundException {
    exception.expect(BrewingProcessNotFoundException.class);
    logger.getMessages();
  }

  /**
   * Validates that the log is finished although it couldnt be saved as we can't do anything about
   * it
   * 
   * @throws LogSavingException
   * @throws BrewingProcessException
   */
  @Category(UnitTest.class)
  @Test
  public void testLogSavingException() throws LogSavingException, BrewingProcessException {
    /*
     * just checks that no exception is thrown
     */
    Mockito.when(logStorage.saveLog(Mockito.any(BrewingLog.class))).thenThrow(
        new LogSavingException("Log could not be safed"));
    logger.startLog(new BrewingProcess(mock(Recipe.class), 1));
    logger.log(mock);
    logger.finishLog();
  }

  /**
   * Tests if no exception is thrown when correctly initialized.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception which could
   *         (but should not) occur
   * @throws BrewingProcessException the brewing process exception which could (but should not)
   *         occur
   */
  @Category(UnitTest.class)
  @Test
  public void testNoExceptionWhenInitialized() throws BrewingProcessNotFoundException,
      BrewingProcessException {
    logger.startLog(new BrewingProcess(mock(Recipe.class), 1));
    logger.log(mock);
    logger.log(mock);
  }
  
  /**
   * Tests the methods getMessages() and getMessagesByPriority and validates, if only the messages
   * with a given priority are returned
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception possible
   *         exception which should not occur in this test
   * @throws BrewingProcessException the brewing process exception possible exception which should
   *         not occur in this test
   */
  @Category(UnitTest.class)
  @Test
  public void testReceivingMessages() throws BrewingProcessNotFoundException,
      BrewingProcessException {
    HopAdditionMessage hopMessageAlways = mock(HopAdditionMessage.class);
    HopAdditionMessage hopMessageLow = mock(HopAdditionMessage.class);
    HopAdditionMessage hopMessageMedium = mock(HopAdditionMessage.class);

    when(hopMessageAlways.getPriority()).thenReturn(MessagePriority.ALWAYS);
    when(hopMessageLow.getPriority()).thenReturn(MessagePriority.LOW);
    when(hopMessageMedium.getPriority()).thenReturn(MessagePriority.MEDIUM);

    logger.startLog(new BrewingProcess(mock(Recipe.class), 1));

    Assert.assertEquals(0, logger.getMessages().size());
    Assert.assertEquals(0, logger.getMessagesByPriority(MessagePriority.ALWAYS).size());
    Assert.assertEquals(0, logger.getMessagesByPriority(MessagePriority.LOW).size());

    logger.log(hopMessageLow);
    Assert.assertEquals(1, logger.getMessages().size());
    Assert.assertEquals(0, logger.getMessagesByPriority(MessagePriority.ALWAYS).size());
    Assert.assertEquals(1, logger.getMessagesByPriority(MessagePriority.LOW).size());

    logger.log(hopMessageAlways);
    Assert.assertEquals(2, logger.getMessages().size());
    Assert.assertEquals(1, logger.getMessagesByPriority(MessagePriority.ALWAYS).size());
    Assert.assertEquals(2, logger.getMessagesByPriority(MessagePriority.LOW).size());

    logger.log(hopMessageMedium);
    Assert.assertEquals(3, logger.getMessages().size());
    Assert.assertEquals(1, logger.getMessagesByPriority(MessagePriority.ALWAYS).size());
    Assert.assertEquals(2, logger.getMessagesByPriority(MessagePriority.MEDIUM).size());
    Assert.assertEquals(3, logger.getMessagesByPriority(MessagePriority.LOW).size());
  }
}
