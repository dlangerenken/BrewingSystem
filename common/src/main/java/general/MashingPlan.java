/*
 *
 */
package general;

import interfaces.IBrewingPartPlan;
import interfaces.IObjectWithValidationStatus;
import utilities.PropertyUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Describes which malts are to be added and the rests during mashing.
 */
public class MashingPlan implements IBrewingPartPlan, IObjectWithValidationStatus {
  /** The temperature levels (time and temperature) during mashing. */
  private List<TemperatureLevel> temperatureLevels;

  /** The malts to add. */
  private List<MaltAddition> maltAdditions;


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
    MashingPlan other = (MashingPlan) obj;
    if (maltAdditions == null) {
      if (other.maltAdditions != null) {
        return false;
      }
    } else if (!maltAdditions.equals(other.maltAdditions)) {
      return false;
    }
    if (temperatureLevels == null) {
      if (other.temperatureLevels != null) {
        return false;
      }
    } else if (!temperatureLevels.equals(other.temperatureLevels)) {
      return false;
    }
    return true;
  }

  /**
   * A mashing plan is invalid if: There are no temperature levels There are no malt additions The
   * temperature levels do not line up to a time-line, e.g. they overlap
   *
   * @return a string containing an error message if the recipe is not valid and null else wise
   */
  @Override
  public String getErrorMessage() {
    final StringBuilder errorMessage = new StringBuilder();
    if ((temperatureLevels == null || temperatureLevels.isEmpty())
        && (maltAdditions == null || maltAdditions.isEmpty())) {
      /* both empty, so nothing to do for mashing */
      return null;
    }
    if (maltAdditions == null || maltAdditions.isEmpty()) {
      return "There are no MaltAdditions, but TemperatureLevels defined. This does not make any sense.";
    }
    if (temperatureLevels == null || temperatureLevels.isEmpty()) {
      return "There are no TemperatureLevels, but MaltAdditions defined. This does not make any sense.";
    }
    /* both lists are not empty */
    String temperatureError = getTemperatureLevelError();
    if (temperatureError != null) {
      errorMessage.append(temperatureError);
    }
    String maltError = getMaltAdditionError();
    if (maltError != null) {
      errorMessage.append(maltError);
    }
    final String error = errorMessage.toString();
    return error.isEmpty() ? null : error;
  }

  /**
   * Gets the malts that have to be added in this plan.
   *
   * @return the malt additions
   */
  public List<MaltAddition> getMaltAdditions() {
    return maltAdditions;
  }

  /**
   * Checks if the malt additions are valid, i.e. they are not sorted.
   * 
   * @return errormessage or null if nothing found
   */
  private String getMaltAdditionError() {
    StringBuilder errorMessage = new StringBuilder();
    MaltAddition lastAddition = null;
    for (MaltAddition maltAddition : maltAdditions) {
      if (maltAddition.getInputTime() < 0) {
        errorMessage.append("The input time for malt additions must be >= 0.\n");
      }
      if (lastAddition != null && lastAddition.getInputTime() > maltAddition.getInputTime()) {
        /* malt additions should be ordered by time */
        errorMessage.append("The hop additions should be ordered ascending: " + "x.inputTime = "
            + lastAddition.getInputTime() + "" + "y.inputTime = " + maltAddition.getInputTime()
            + "\n");
      }
      lastAddition = maltAddition;
    }
    final String error = errorMessage.toString();
    return error.isEmpty() ? null : error;
  }

  /**
   * Checks if the temperature levels are valid, i.e. they do not overlap
   * 
   * @return errormessage or null if nothing found
   */
  private String getTemperatureLevelError() {
    StringBuilder errorMessage = new StringBuilder();
    TemperatureLevel lastTempLevel = null;
    for (TemperatureLevel tempLevel : temperatureLevels) {
      if (tempLevel.getStartTime() < 0 || tempLevel.getDuration() < 0) {
        errorMessage.append("A start time < 0  or duration < 0 is invalid.\n");
      }
      int tempMin = 18;
      int tempMax = 100;
      if (tempLevel.getTemperature() < tempMin || tempLevel.getTemperature() > tempMax) {
        errorMessage.append("The temperature level must be between " + tempMin + " and " + tempMax
            + ".\n");
      }
      if (lastTempLevel != null
          && lastTempLevel.getStartTime() + lastTempLevel.getDuration() > tempLevel.getStartTime()) {

        /* no overlapping TemperatureLevels allowed */
        errorMessage.append("There are two overlapping tempLevels: " + "x.startTime = "
            + lastTempLevel.getStartTime() + "" + " x.duration = " + lastTempLevel.getDuration()
            + "" + "y.startTime = " + tempLevel.getStartTime() + "\n");
      }

      if (tempLevel.getTemperature() < PropertyUtil.getRoomTemperature()) {
        /* Temperature level below room temperature is not allowed. */
        errorMessage.append("There is a temperature level of "
            + String.valueOf(tempLevel.getTemperature())
            + "°C which is below the room temperature of " + String.valueOf(PropertyUtil.getRoomTemperature())
            + "°C");
      }
      lastTempLevel = tempLevel;
    }
    final String error = errorMessage.toString();
    return error.isEmpty() ? null : error;
  }

  /**
   * Gets the temperature levels of this mashing plan.
   *
   * @return the temperature levels
   */
  public List<TemperatureLevel> getTemperatureLevels() {
    return temperatureLevels;
  }

  /**
   * Instantiates a new mashing plan.
   */
  public MashingPlan() {
    temperatureLevels = new ArrayList<>();
    maltAdditions = new ArrayList<>();
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((maltAdditions == null) ? 0 : maltAdditions.hashCode());
    result = prime * result + ((temperatureLevels == null) ? 0 : temperatureLevels.hashCode());
    return result;
  }

  /**
   * Sets the malts that are added in this plan.
   *
   * @param maltAdditions the new malt additions. Should not be null.
   */
  public void setMaltAdditions(final List<MaltAddition> maltAdditions) {
    if (maltAdditions == null) {
      throw new IllegalArgumentException("maltAdditions should not be null");
    }
    this.maltAdditions = maltAdditions;
  }

  /**
   * Sets the temperature levels that are held in this plan.
   *
   * @param temperatureLevels the new temperature levels. Should not be null.
   */
  public void setTemperatureLevels(final List<TemperatureLevel> temperatureLevels) {
    if (temperatureLevels == null) {
      throw new IllegalArgumentException("temperatureLevels should not be null");
    }
    this.temperatureLevels = temperatureLevels;
  }
}
