/*
 *
 */
package general;


/**
 * A time and temperature of a rest during mashing
 */
public class TemperatureLevel {

  /** The time since the beginning of mashing in milliseconds this rest starts. */
  private long startTime;

  /** The temperature of the rest (in °C). */
  private float temperature; // should we add a unit here?

  /** The duration of the rest. */
  private long duration;

  /** creates a TemperatureLevel with none of its fields set. */
  public TemperatureLevel() {}

  /** creates a TemperatureLevel with all fields set to the given values */
  public TemperatureLevel(final long duration, final long startTime, final float temperature) {
    this.duration = duration;
    this.startTime = startTime;
    this.temperature = temperature;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TemperatureLevel other = (TemperatureLevel) obj;
    if (duration != other.duration) {
      return false;
    }
    if (startTime != other.startTime) {
      return false;
    }
    if (Float.floatToIntBits(temperature) != Float.floatToIntBits(other.temperature)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the duration this temperature level is held for.
   *
   * @return the duration
   */
  public long getDuration() {
    return duration;
  }

  /**
   * Gets the start time in ms since the beginning of mashing.
   *
   * @return the start time
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * Gets the temperature in °C.
   *
   * @return the temperature
   */
  public float getTemperature() {
    return temperature;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) duration;
    result = prime * result + (int) (startTime ^ (startTime >>> 32));
    result = prime * result + Float.floatToIntBits(temperature);
    return result;
  }

  /**
   * Sets the duration.
   *
   * @param duration the new duration
   */
  public void setDuration(final long duration) {
    this.duration = duration;
  }

  /**
   * Sets the start time (ms) since the beginning of mashing.
   *
   * @param startTime the new start time
   */
  public void setStartTime(final long startTime) {
    this.startTime = startTime;
  }

  /**
   * Sets the temperature in °C.
   *
   * @param temperature the new temperature
   */
  public void setTemperature(final float temperature) {
    this.temperature = temperature;
  }
}
