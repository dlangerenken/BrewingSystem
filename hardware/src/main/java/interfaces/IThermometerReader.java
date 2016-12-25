/*
 * 
 */
package interfaces;

import exceptions.TemperatureNotReadableException;


// renamed from "GetTemperature"
/**
 * The Interface IThermometerReader.
 */
public interface IThermometerReader {

  /**
   * Gets the temperature.
   *
   * @return the temperature
   * @throws TemperatureNotReadableException the temperature not readable exception
   */
  public float getTemperature() throws TemperatureNotReadableException;

}
