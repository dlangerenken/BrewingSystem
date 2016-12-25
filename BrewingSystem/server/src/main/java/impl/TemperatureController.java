/*
 * 
 */
package impl;

import exceptions.TemperatureNotReadableException;
import general.HardwareStatus;
import interfaces.IHeaterControl;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureService;
import interfaces.IThermometerReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;
import utilities.TemperatureListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Provides methods towards controlling temperature.
 */
@Singleton
public class TemperatureController implements ITemperatureService {

  /**
   * Responsible for holding a temperature level.
   * 
   * @author max
   *
   */
  private class TemperatureMaintainer extends Thread {
    /**
     * current temperature which was measured
     */
    protected float currentTemperature = 0.0f;

    /** The heatup temperature. */
    private float heatupTemperature;

    /**
     * The delta which is allowed to inform listeners (even though the actual desired temperature is
     * not reached yet)
     */
    private final float temperatureHeatUpDelta;

    /**
     * Instantiates a new temperature maintainer.
     *
     * @param heatupTemperature the heatup temperature
     */
    public TemperatureMaintainer(final float newTemperature) {
      updateTemperature(newTemperature);
      temperatureHeatUpDelta = PropertyUtil.getTempcHeatupDelta();
    }

    /**
     * updates the current desired temperature
     * 
     * @param newTemperature new temperature which should be reached
     */
    public void updateTemperature(final float newTemperature) {
      heatupTemperature = newTemperature;
    }

    /**
     * Notifies listener which are waiting for the current temperature
     * 
     * @param currentTemperature the current temperature given by the thermometer
     */
    private void broadcastNotifications() {
      subscribersLock.lock();

      for (final Iterator<TemperatureListener> iterator = subscribers.iterator(); iterator
          .hasNext();) {
        TemperatureListener listener = iterator.next();
        /*
         * calculate min and maximum temperature range
         */
        int lowerLimit = listener.getTemperature() - listener.getDelta();
        int upperLimit = listener.getTemperature() + listener.getDelta();

        /*
         * check if the current temperature is inside the limit
         */

        if (lowerLimit <= currentTemperature && currentTemperature <= upperLimit) {
          ITemperatureEvent.SubscribeStatus subscribeStatus =
              listener.getNotify().temperatureReached(currentTemperature);

          switch (subscribeStatus) {
            case SUBSCRIBE:
              /*
               * do nothing as the listener still wants to get notifications
               */
              break;
            case UNSUBSCRIBE:
              /*
               * remove listener from subscribers as it does not want to receive any further
               * notifications
               */
              iterator.remove();
              break;
          }
        }
      }
      subscribersLock.unlock();
    }

    /**
     * Control heater depending on the current temperature.
     *
     * @param currentTemperature the current temperature
     */
    void controlHeater() {
      /*
       * if temperature is lower than expected, we need to turn on the heater (turn off otherwise)
       */
      if (currentTemperature < heatupTemperature - temperatureHeatUpDelta) {
        if (!heaterControl.isSwitchedOn()) {
          // Log notification that we turned on the heater.
          LOGGER.info("Switched ON heater, temp. " + String.valueOf(currentTemperature) + " ->> "
              + String.valueOf(heatupTemperature));
        }
        heaterControl.switchOn();
      } else if (currentTemperature > heatupTemperature + temperatureHeatUpDelta) {
        if (!heaterControl.isSwitchedOn()) {
          // Log notification that we turned on the heater.
          LOGGER.info("Switched OFF heater, temp. " + String.valueOf(currentTemperature) + " ->> "
              + String.valueOf(heatupTemperature));
        }
        heaterControl.switchOff();
      }
    }

    /**
     * Checks the temperature and controls the heater/notifies observers
     */
    @Override
    public void run() {
      int readerErrors = 0;

      while (true) {
        try {
          currentTemperature = thermometerReader.getTemperature();
          temperatureStatus = HardwareStatus.ENABLED;
          readerErrors = 0;
          controlHeater();
          broadcastNotifications();
        } catch (TemperatureNotReadableException e1) {
          readerErrors += 1;
          TemperatureController.LOGGER.error(String.format(
              "Temperature could not be read. This is the %d time in a row", readerErrors), e1);
          temperatureStatus = HardwareStatus.DISABLED;

          /*
           * interrupt heater control if it fails to read ten times in a row
           */
          if (readerErrors >= 10) {
            heaterControl.switchOff();
            temperatureStatus = HardwareStatus.NOT_FOUND;
            TemperatureController.LOGGER.error(String.format(
                "Unable to read temperature %d times in a row! Stopping Heater Process.",
                readerErrors), e1);
            return;
          }
        }

        try {
          Thread.sleep(TemperatureController.MEASUREMENT_INTERVAL);
        } catch (InterruptedException e) {
          // Nicht loggen, weil beim stop()en des TemperatureControllers wird
          // die interruptedexception zwangsweise aufgerufen.
          return;
        }
      }
    }
  }

  /** The Constant logger. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * The interval that lies between two temperature measurements.
   */
  public static final int MEASUREMENT_INTERVAL = 1500;

  /** The heater control. */
  private final IHeaterControl heaterControl;

  /** The thermometer reader. */
  private final IThermometerReader thermometerReader;
  /**
   * The Thread that is currently being used to control the temperature.
   */
  private TemperatureMaintainer temperatureMaintainerThread = null;

  /** The subscribers lock. */
  private final Lock subscribersLock = new ReentrantLock();

  /** The subscribers. */
  private final List<TemperatureListener> subscribers = new ArrayList<>();

  /** The heater status. */
  private final HardwareStatus heaterStatus = HardwareStatus.DISABLED;

  /** The temperature status. */
  private HardwareStatus temperatureStatus = HardwareStatus.DISABLED;

  /**
   * Instantiates a new temperature controller.
   *
   * @param thermometerReader the thermometer reader
   * @param heaterControl the heater control
   */
  @Inject
  public TemperatureController(final IThermometerReader thermometerReader,
      final IHeaterControl heaterControl) {
    this.heaterControl = heaterControl;
    this.thermometerReader = thermometerReader;
    LOGGER.info("TemperatureController constructed");
  }

  /**
   * Destructor that ensures that heating process is cancelled if temperatur controller is
   * destroyed.
   */
  @Override
  protected void finalize() {
    this.stop();
  }


  @Override
  public HardwareStatus getHeaterStatus() {
    if (this.heaterStatus == HardwareStatus.NOT_FOUND) {
      // If hardware was not found, return this as error message.
      return this.heaterStatus;
    } else {
      // If hardware seems to be connected, return whether the heater
      // is switched on or off.
      if (this.heaterControl.isSwitchedOn()) {
        return HardwareStatus.ENABLED;
      } else {
        return HardwareStatus.DISABLED;
      }
    }
  }


  @Override
  public Float getTemperature() {
    try {
      return this.thermometerReader.getTemperature();
    } catch (TemperatureNotReadableException e) {
      /*
       * No temperature received, return null to let the caller no that no temperature is available
       */
      LOGGER.error(e);
      return null;
    }
  }


  @Override
  public HardwareStatus getTemperatureSensorStatus() {
    return temperatureStatus;
  }

  /**
   * Heats up to the specified temperature and maintains it. To cool down, call heatUp with a value
   * of zero or below.
   *
   * @param temperature the temperature
   */
  @Override
  public void heatUp(final int temperature) {
    /*
     * starts a new thread if the heater thread is not alive, updates temperature otherwise
     */
    if (temperatureMaintainerThread != null && temperatureMaintainerThread.isAlive()) {
      temperatureMaintainerThread.updateTemperature(temperature);
    } else {
      if (temperatureMaintainerThread != null) {
        // Thread exists but not alive.
        temperatureMaintainerThread.interrupt();
      }
      temperatureMaintainerThread = new TemperatureMaintainer(temperature);
      temperatureMaintainerThread.start();
    }
  }

  /**
   * Terminates the heating/cooling process if still running.
   */
  @Override
  public void stop() {
    LOGGER.info("Temperature Controller stopped");
    if (temperatureMaintainerThread != null) {
      temperatureMaintainerThread.interrupt();
      temperatureMaintainerThread = null;
    }
    subscribersLock.lock();
    subscribers.clear();
    subscribersLock.unlock();

    // Ensure that the heater is also turned off.
    heaterControl.switchOff();
  }

  /**
   * Registers a new listener that gets notified, when the temperature reaches an interval around +-
   * delta of the specified temperature.
   *
   * @param temperature the temperature
   * @param delta the delta
   * @param notify the notify
   */
  @Override
  public void subscribe(final int temperature, final int delta, final ITemperatureEvent notify) {
    subscribersLock.lock();
    subscribers.add(new TemperatureListener(temperature, delta, notify));
    subscribersLock.unlock();
  }

  /**
   * Deletes a listener that has previously been registered.
   *
   * @param notify the notify
   */
  @Override
  public void unsubscribe(final ITemperatureEvent notify) {
    if (notify != null) {
      subscribersLock.lock();

      /*
       * remove corresponding temperature listener
       */
      for (final Iterator<TemperatureListener> iterator = subscribers.iterator(); iterator
          .hasNext();) {
        TemperatureListener listener = iterator.next();
        if (listener.getNotify() == notify) {
          iterator.remove();
        }
      }
      subscribersLock.unlock();
    }
  }
}
