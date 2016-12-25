/*
 *
 */
package general;

import interfaces.IBrewingPartPlan;
import interfaces.IObjectWithValidationStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * HopCookingPlan defines the timing of the hop additions and the duration of the entire hop
 * cooking.
 */
public class HopCookingPlan implements IBrewingPartPlan, IObjectWithValidationStatus {

  /** List of hops and their time to add. */
  private List<HopAddition> hopAdditions;

  /** The duration of the entire hop cooking process in milliseconds */
  private long duration;



  /**
   * A hop cooking plan is invalid if: There are no hop additions The hop additions do not line up
   * to a time-line, e.g. they overlap
   *
   * @return a string containing an error message if the recipe is not valid and null else wise
   */
  @Override
  public String getErrorMessage() {
    final StringBuilder errorMessage = new StringBuilder();
    if (hopAdditions == null || hopAdditions.isEmpty()) {
      /* an empty hop cooking plan is valid */
      return null;
    }
    if (duration <= 0) {
      errorMessage.append("The duration has to be > 0\n");
    }
    HopAddition lastAddition = null;
    for (HopAddition hopAddition : hopAdditions) {
      if (hopAddition.getInputTime() < 0) {
        errorMessage.append("The input time must be >= 0\n");
      }
      if (lastAddition != null && lastAddition.getInputTime() > hopAddition.getInputTime()) {
        errorMessage.append("The hop additions should be ordered ascending: " + "x.inputTime = "
            + lastAddition.getInputTime() + "" + "y.inputTime = " + hopAddition.getInputTime()
            + "\n");
      }
      lastAddition = hopAddition;
    }
    if (lastAddition.getInputTime() > duration) {
      errorMessage.append("The duration has to be > lastHopAddition.inputTime()\n");
    }
    final String error = errorMessage.toString();
    return error.isEmpty() ? null : error;
  }

  /**
   * Instantiates a new hop cooking plan.
   */
  public HopCookingPlan() {
    hopAdditions = new ArrayList<>();
  }

  /**
   * List of all hop-additions.
   *
   * @return hop-addition set (hop, timeToAdd)
   */
  public List<HopAddition> getHopAdditions() {
    return hopAdditions;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (duration ^ (duration >>> 32));
    result = prime * result + ((hopAdditions == null) ? 0 : hopAdditions.hashCode());
    return result;
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
    HopCookingPlan other = (HopCookingPlan) obj;
    if (duration != other.duration) {
      return false;
    }
    if (hopAdditions == null) {
      if (other.hopAdditions != null) {
        return false;
      }
    } else if (!hopAdditions.equals(other.hopAdditions)) {
      return false;
    }
    return true;
  }

  /**
   * Sets the list of all hop-additions.
   *
   * @param hopAdditions hopAdditions for the hop-cooking plan
   */
  public void setHopAdditions(final List<HopAddition> hopAdditions) {
    if (hopAdditions == null) {
      throw new IllegalArgumentException("hopAdditions should not be null");
    }
    this.hopAdditions = hopAdditions;
  }

  /** sets the duration */
  public long getDuration() {
    return duration;
  }

  /** gets the duration */
  public void setDuration(final long duration) {
    this.duration = duration;
  }
}
