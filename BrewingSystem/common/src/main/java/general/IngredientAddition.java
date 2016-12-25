/*
 *
 */
package general;


/**
 * HopAddition as an ingredient for the brewing process.
 */
public class IngredientAddition extends Ingredient {



  /** Time when the hop should be added, from the beginning of hop cooking (in ms). */
  private long inputTime;

  /**
   * constructor with all fields
   *
   * @param amount the amount of hop in "unit"
   * @param unit the unit
   * @param name the name of the hop that is to be added
   * @param inputTime the time when the hop must be inserted after the start of the hop cooking
   *        process.
   */
  public IngredientAddition(final float amount, final Unit unit, final String name,
      final long inputTime) {
    super(amount, unit, name);
    setInputTime(inputTime);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    IngredientAddition other = (IngredientAddition) obj;
    if (inputTime != other.inputTime) {
      return false;
    }
    return true;
  }

  /**
   * Time when the hop should be added, from the beginning of hop cooking (in ms).
   *
   *
   * @return time in milliseconds when to add the hop
   */
  public long getInputTime() {
    return inputTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (inputTime ^ (inputTime >>> 32));
    return result;
  }

  /**
   * Sets the input time for the hop addition (ms after start of hop cooking).
   *
   * @param inputTime when to add the hop value
   */
  public void setInputTime(final long inputTime) {
    this.inputTime = inputTime;
  }

}
