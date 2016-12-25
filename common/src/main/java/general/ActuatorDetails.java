/*
 * 
 */
package general;

import java.io.Serializable;


/**
 * Stores information about heater, stirrer and temperature-sensor.
 *
 * @author Daniel Langerenken
 */
public class ActuatorDetails implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** Current status of the heater. */
  private HardwareStatus heaterStatus;

  /** Current status of the stirrer. */
  private HardwareStatus stirrerStatus;

  /** Current status of the temperature-sensor. */
  private HardwareStatus temperatureSensorStatus;

  /** Current temperature (null, if sensor is disconnected). */
  private Float temperature;

  /**
   * Returns the heater status.
   *
   * @return enabled, disabled or not connected
   */
  public HardwareStatus getHeaterStatus() {
    return heaterStatus;
  }

  /**
   * Returns the stirrer status.
   *
   * @return enabled, disabled or not connected
   */
  public HardwareStatus getStirrerStatus() {
    return stirrerStatus;
  }

  /**
   * returns the temperature which can be null in case the sensor is not connected.
   *
   * @return temperature between 0.0 - 100.0 or null, if sensor is not connected
   */
  public Float getTemperature() {
    return temperature;
  }

  /**
   * Returns the temperature status.
   *
   * @return enabled, disabled or not connected
   */
  public HardwareStatus getTemperatureSensorStatus() {
    return temperatureSensorStatus;
  }

  /**
   * sets the heater status.
   *
   * @param heaterStatus (enabled, disabled or not connected)
   */
  public void setHeaterStatus(final HardwareStatus heaterStatus) {
    this.heaterStatus = heaterStatus;
  }

  /**
   * sets the stirrer status.
   *
   * @param stirrerStatus (enabled, disabled or not connected)
   */
  public void setStirrerStatus(final HardwareStatus stirrerStatus) {
    this.stirrerStatus = stirrerStatus;
  }

  /**
   * sets the temperature (null allowed).
   *
   * @param temperature temperature between 0.0 - 100.0 or null
   */
  public void setTemperature(final Float temperature) {
    this.temperature = temperature;
  }

  /**
   * sets the temperature-sensor status.
   *
   * @param temperatureSensorStatus (enabled, disabled or not connected)
   */
  public void setTemperatureSensorStatus(final HardwareStatus temperatureSensorStatus) {
    this.temperatureSensorStatus = temperatureSensorStatus;
  }

}
