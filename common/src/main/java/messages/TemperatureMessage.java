/*
 *
 */
package messages;

import general.MessagePriority;



/**
 * A message to notify about a temperature. Created by Daniel on 18.12.2014.
 */
public class TemperatureMessage extends Message {

  /** The temperature. */
  private float temperature;

  /** Creates a new {@link TemperatureMessage} with very low priority */
  public TemperatureMessage(final float temp) {
    temperature = temp;
    setPriority(MessagePriority.VERY_LOW);
  }

  /**
   * Gets the temperature.
   *
   * @return the temperature
   */
  public float getTemperature() {
    return temperature;
  }

  /**
   * Sets the temperature.
   *
   * @param temperature the new temperature
   */
  public void setTemperature(final float temperature) {
    this.temperature = temperature;
  }
}
