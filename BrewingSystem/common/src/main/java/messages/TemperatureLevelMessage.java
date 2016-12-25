/*
 *
 */
package messages;

import general.MessagePriority;
import general.TemperatureLevel;


/**
 * Is send when the holding of a temperature level during mashing (aka rest) is beginning.
 */
public class TemperatureLevelMessage extends MashingMessage {

  /** The temperature level. */
  private final TemperatureLevel temperatureLevel;

  /**
   * Creates a temperature level message.
   *
   * @param temperatureLevel
   */
  public TemperatureLevelMessage(final TemperatureLevel temperatureLevel) {
    setPriority(MessagePriority.LOW);
    this.temperatureLevel = temperatureLevel;
  }

  /**
   * Gets the temperature level.
   *
   * @return the temperature level
   */
  public TemperatureLevel getTemperatureLevel() {
    return temperatureLevel;
  }
}
