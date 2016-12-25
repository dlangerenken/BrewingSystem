/*
 *
 */
package general;


/**
 * The Class IodineTest.
 */
public class IodineTest {

  /** The waiting period. */
  private final int waitingPeriodInSeconds;

  /**
   * Creates a new IodineTest result.
   * 
   * @param result whether the test was positive
   * @param waitingPeriodInSeconds the time to wait until performing the next iodine test (0 if positive)
   */
  public IodineTest(final int waitingPeriodInSeconds) {
    this.waitingPeriodInSeconds = waitingPeriodInSeconds;
  }


  /**
   * Gets the length of the waiting period.
   *
   * @return the waiting period
   */
  public int getWaitingPeriod() {
    return waitingPeriodInSeconds;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + waitingPeriodInSeconds;
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
    IodineTest other = (IodineTest) obj;
    if (waitingPeriodInSeconds != other.waitingPeriodInSeconds) {
      return false;
    }
    return true;
  }


  /**
   * Checks if result is positive
   *
   * @return true, if result is positive
   */
  public boolean isPositive() {
    return waitingPeriodInSeconds <= 0;
  }
}
