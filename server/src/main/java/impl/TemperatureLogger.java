package impl;

import interfaces.IBrewingLogService;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureLogger;
import interfaces.ITemperatureService;
import messages.TemperatureMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import exceptions.BrewingProcessNotFoundException;

/**
 * Provides functionality to peridiodically log the current temperature reported by the temperature
 * sensor into the brewing log.
 * 
 * @author max
 *
 */
@Singleton
public class TemperatureLogger implements ITemperatureLogger {

  /** The logger which saves all the information into log files */
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * the temperature service which is used for receiving the current temperature
   */
  private ITemperatureService tempService;

  /**
   * the brewing log service which is used for logging messages
   */
  private final IBrewingLogService brewingLogService;

  /**
   * Delta of the logging temperature which is allowed to differ from the real value
   */
  private TemperatureListener currentTempListener;

  /**
   * Delta of the logging temperature which is allowed to differ from the real value
   */
  private final int loggingTempDelta;

  /**
   * Delta of the logging time which is allowed to differ from the real value
   */
  private final long loggingTimeDelta;

  /**
   * Creates a new temperature logger.
   * 
   * @param brewingLogService
   */
  @Inject
  public TemperatureLogger(final IBrewingLogService brewingLogService) {
    loggingTempDelta = PropertyUtil.getTemperatureLoggingTemperatureDelta();
    loggingTimeDelta =
        PropertyUtil.getPropertyLong(PropertyUtil.TEMPERATURE_LOGGING_TIME_DELTA_PROPERTY);
    this.brewingLogService = brewingLogService;
  }


  @Override
  public void subscribeToTemperatureController(final ITemperatureService tc) {
    if (currentTempListener != null) {
      unsubscribe();
    }

    tempService = tc;
    currentTempListener = new TemperatureListener();

    /*
     * Register for any temperature between -50° to 150° so that every notification is handled
     */
    tc.subscribe(50, 100, currentTempListener);
  }


  @Override
  public void unsubscribe() {
    if (tempService != null) {
      tempService.unsubscribe(currentTempListener);
    }
    currentTempListener = null;
  }

  /**
   * Log temperature changes
   *
   * @author max
   *
   */
  private class TemperatureListener implements ITemperatureEvent {

    /**
     * The last received temperature (or null, if no message was received yet)
     */
    private Float lastTemperature;

    /**
     * The time when the last log was received
     */
    private long lastLog = -1;

    /**
     * Instantiates the temperature listener and sets the last temperature to null
     */
    public TemperatureListener() {
      lastTemperature = null;
    }

    @Override
    public SubscribeStatus temperatureReached(final float temperature) {
      LOGGER.info(String.format("Temp: %.2f°C", temperature));
      long currentTime = System.currentTimeMillis();
      if (lastTemperature != null && Math.abs(temperature - lastTemperature) < loggingTempDelta
          && lastLog >= 0 && currentTime - lastLog < loggingTimeDelta) {
        /*
         * Temperature did not change significantly and time passed since last log is less than the
         * threshold, keep subscribed
         */
        return SubscribeStatus.SUBSCRIBE;
      }
      lastTemperature = temperature;
      lastLog = currentTime;
      TemperatureMessage temperatureMessage = new TemperatureMessage(temperature);
      temperatureMessage.setMessage(String.format("The current temperature has changed to %f.",
          lastTemperature));
      try {
        brewingLogService.log(temperatureMessage);
      } catch (BrewingProcessNotFoundException e) {
        LOGGER.error("BrewingProcess was not found", e);
      }

      /*
       * keep subscribed
       */
      return SubscribeStatus.SUBSCRIBE;
    }
  }
}
