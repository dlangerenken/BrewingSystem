/*
 *
 */
package general;


/**
 * A manual step which has to by the brewer in the brewing process.
 */
public class ManualStep {

  /** The description of the task. */
  private String description;

  /** The time when the step should be performed */
  private long startTime;

  /** The duration. */
  private long duration;

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
    ManualStep other = (ManualStep) obj;
    if (description == null) {
      if (other.description != null) {
        return false;
      }
    } else if (!description.equals(other.description)) {
      return false;
    }
    if (duration != other.duration) {
      return false;
    }
    if (startTime != other.startTime) {
      return false;
    }
    return true;
  }

  /**
   * Gets the description of the task.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the duration.
   *
   * @return the duration
   */
  public long getDuration() {
    return duration;
  }

  /**
   * Gets the time when this task should be performed.
   *
   * @return the start time
   */
  public long getStartTime() {
    return startTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + (int) (duration ^ (duration >>> 32));
    result = prime * result + (int) (startTime ^ (startTime >>> 32));
    return result;
  }

  /**
   * Sets the description of the task.
   *
   * @param description the new description
   */
  public void setDescription(final String description) {
    this.description = description;
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
   * Sets the time when this step should be started.
   *
   * @param startTime the new start time
   */
  public void setStartTime(final long startTime) {
    this.startTime = startTime;
  }
}
