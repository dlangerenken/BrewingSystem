/*
 * 
 */
package interfaces;


/**
 * The Interface ITemperatureEvent.
 */
public interface ITemperatureEvent {

  enum SubscribeStatus {
    SUBSCRIBE, UNSUBSCRIBE
  };

  /**
   * Temperature reached.
   *
   * @param temperature the temperature
   * @return desired subscribe status afterwards
   */
  SubscribeStatus temperatureReached(float temperature);
}
