package utilities;

import interfaces.ITemperatureEvent;

/**
 * Speichert zu jedem subscriber, welche Temperatur und welches Delta er wissen möchte.
 * 
 * @author max
 *
 */
public class TemperatureListener {

  /** The temperature. */
  private final int temperature;

  /** The delta. */
  private final int delta;

  /** The notify. */
  private final ITemperatureEvent notify;

  /**
   * Instantiates a new temperature listener.
   *
   * @param temperature the temperature
   * @param delta the delta
   * @param notify the notify
   */
  public TemperatureListener(final int temperature, final int delta, final ITemperatureEvent notify) {
    this.temperature = temperature;
    this.delta = delta;
    this.notify = notify;
  }

  /**
   * Gets the delta.
   *
   * @return the delta
   */
  public int getDelta() {
    return delta;
  }

  /**
   * Gets the notify.
   *
   * @return the notify
   */
  public ITemperatureEvent getNotify() {
    return notify;
  }

  /**
   * Gets the temperature.
   *
   * @return the temperature
   */
  public int getTemperature() {
    return temperature;
  }

}
