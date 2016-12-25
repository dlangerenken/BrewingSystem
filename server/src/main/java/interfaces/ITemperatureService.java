/*
 * 
 */
package interfaces;

import general.HardwareStatus;


/**
 * The Interface ITemperatureService.
 */
public interface ITemperatureService {

  /**
   * whether the heater is enabled/disabled or not connected.
   *
   * @return enabled/disabled/not connected
   */
  HardwareStatus getHeaterStatus();

  /**
   * Gets the temperature.
   *
   * @return the temperature
   */
  Float getTemperature();

  /**
   * whether the temperature sensor is enabled/disabled or not connected.
   *
   * @return enabled/disabled/not connected
   */
  HardwareStatus getTemperatureSensorStatus();

  /**
   * Heat up.
   *
   * @param temperature the temperature
   */
  void heatUp(int temperature);

  /**
   * Terminates the heating/cooling process if still running.
   */
  void stop();

  /**
   * Subscribe.
   *
   * @param temperature the temperature
   * @param delta the delta
   * @param notify the notify
   */
  void subscribe(int temperature, int delta, ITemperatureEvent notify);

  /**
   * Unsubscribe.
   *
   * @param notify the notify
   */
  void unsubscribe(ITemperatureEvent notify);

}
