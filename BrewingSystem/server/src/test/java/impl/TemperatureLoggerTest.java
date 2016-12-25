package impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import interfaces.IBrewingLogService;
import interfaces.ILogStorage;
import interfaces.ITemperatureLogger;
import interfaces.ITemperatureService;
import junit.framework.Assert;
import messages.Message;
import messages.TemperatureMessage;
import mocks.MockHeatableThermometer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.DummyBuilder;
import exceptions.BrewingProcessException;
import general.BrewingLog;
import general.BrewingProcess;

/**
 * Tests the TemperatureLogger with the MockTemperatureController and a mocked brewing log.
 */
@RunWith(MockitoJUnitRunner.class)
public class TemperatureLoggerTest {

  /**
   * Time which is required to heat up
   */
  private static final int TIME_TO_HEAT_UP = 20 * 1000;

  @Test
  public void performTest() throws InterruptedException, BrewingProcessException {
    BrewingProcess brewingProcess = mock(BrewingProcess.class);
    BrewingLog brewingLog = new BrewingLog(DummyBuilder.getRealisticRecipe(), 1);
    when(brewingProcess.getBrewingLog()).thenReturn(brewingLog);

    IBrewingLogService logger = new BrewingLogger(mock(ILogStorage.class));
    logger.startLog(brewingProcess);

    ITemperatureLogger temperatureLogger = new TemperatureLogger(logger);

    /*
     * Attach a temperature service to the temperature-logging and heat up to 50°
     */
    MockHeatableThermometer mht = new MockHeatableThermometer();
    ITemperatureService temperatureService = new TemperatureController(mht, mht);
    temperatureLogger.subscribeToTemperatureController(temperatureService);
    temperatureService.heatUp(50);

    /*
     * Wait until the it's heated up
     */
    Thread.sleep(TIME_TO_HEAT_UP);

    int messagesSizeBefore = 0;
    int messagesSizeAfter = 0;

    Assert.assertTrue("No messages sent! Logger has no messages", logger.getMessages().size() > 0);

    for (Message m : logger.getMessages()) {
      if (!(m instanceof TemperatureMessage)) {
        /*
         * skip unnecessary messages
         */
        continue;
      }
      /*
       * Check if temperature message has a valid temperature
       */
      TemperatureMessage temperatureMessage = (TemperatureMessage) m;
      Assert.assertTrue("Message with wrong temperature", temperatureMessage.getTemperature() <= 60
          && temperatureMessage.getTemperature() > 0);
      messagesSizeBefore += 1;
    }

    /*
     * heat up to 80° and wait until it is heated up
     */
    temperatureLogger.unsubscribe();
    temperatureService.heatUp(80);
    Thread.sleep(TIME_TO_HEAT_UP);

    for (Message m : logger.getMessages()) {
      /*
       * Count all temperature messages within logger.getMessages()
       */
      if (m instanceof TemperatureMessage) {
        messagesSizeAfter += 1;
      }
    }

    /*
     * We expect that no more temperature messages are added to the list
     */
    Assert.assertEquals(messagesSizeBefore, messagesSizeAfter);
  }

}
