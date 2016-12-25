package mocks;

import exceptions.TemperatureNotReadableException;
import interfaces.IHeaterControl;
import interfaces.IThermometerReader;

/**
 * A really awesom class that is used to test the TemperatureController. It implements heater and
 * thermometer, so that we simulate that the temperature raises when the heater is turned on and
 * falls, when heater is turned off.
 * 
 * @author max
 *
 */
public class MockHeatableThermometer implements IHeaterControl, IThermometerReader {

  private boolean heaterStatus;

  private boolean instantHeatup;

  private Float instantHeatupTemperature;

  private float lastTemperature;

  /**
   * If we are in a normal room, the temperature starts probably with 15°C.
   */
  private final float tempLowerBorder = 15;

  /**
   * The maximum heatup difference.
   */
  private final float maxHeatUpDiff = 4.0f;

  /**
   * The maximum cooldown difference.
   */
  private final float maxCooldownDiff = 2.0f;

  private boolean throwBehavior = false;

  /**
   * Instantiates the MockHeatableThermometer
   */
  public MockHeatableThermometer() {
    this.lastTemperature = tempLowerBorder;
    this.heaterStatus = false;
    this.instantHeatup = false;
  }

  /**
   * Gets whether instant heatup is enabled or not.
   * 
   * @param value
   * @return
   */
  public boolean getInstantHeatup() {
    return this.instantHeatup;
  }

  @Override
  public float getTemperature() throws TemperatureNotReadableException {
    if (this.throwBehavior) {
      throw new TemperatureNotReadableException(new Exception("Your mudda!"));
    }

    // For some test purposes we do not want time to elapse while heating up and
    // instead directly return the desired temperature.
    if (this.instantHeatup && instantHeatupTemperature != null) {
      return instantHeatupTemperature;
    }

    float returnTemperature;

    if (this.heaterStatus) {
      // Heater is switched on, so return a higher temperature.
      this.lastTemperature += Math.random() * maxHeatUpDiff;
      returnTemperature = this.lastTemperature;
    } else {
      // Heater is switched off, so return a lower temperature but not less than
      // the TEMP_LOWER_BORDER.
      this.lastTemperature =
          (float) Math.max(this.lastTemperature - (Math.random() * maxCooldownDiff),
              this.tempLowerBorder);
      returnTemperature = this.lastTemperature;
    }

    return returnTemperature;
  }

  @Override
  public boolean isSwitchedOn() {
    return this.heaterStatus;
  }

  /**
   * If called with true, this class always returns the supplied temperature. If called with false,
   * this class tries to behave like a real thermometer and returns a different temperature every
   * time the getTemperature-Method is called.
   * 
   * @param value
   */
  public void setInstantHeatup(final boolean value, final Float temperature) {
    this.instantHeatup = value;
    this.instantHeatupTemperature = temperature;
  }

  /**
   * If you set throw behavior to true, this class will throw a TemperatureNotReadableException in
   * order to test failure tolerance of connected components.
   * 
   * @param value
   */
  public void setThrowBehavior(final boolean value) {
    this.throwBehavior = value;
  }

  @Override
  public void switchOff() {
    synchronized (this) {
      this.heaterStatus = false;
    }
  }

  @Override
  public void switchOn() {
    synchronized (this) {
      this.heaterStatus = true;
    }
  }
}
