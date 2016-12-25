/*
 *
 */
package general;


/**
 * HopAddition as an ingredient for the brewing process.
 */
public class HopAddition extends IngredientAddition {

  /**
   * constructor with all fields
   *
   * @param amount the amount of hop in "unit"
   * @param unit the unit
   * @param name the name of the hop that is to be added
   * @param inputTime the time when the hop must be inserted after the start of the hop cooking
   *        process.
   */
  public HopAddition(float amount, Unit unit, String name, long inputTime) {
    super(amount, unit, name, inputTime);
  }

  /**
   * Empty constructor for parsing. Do NOT use this as default constructor.
   */
  @Deprecated
  public HopAddition() {
    this(0f, Unit.kg, "", 0);
  }
}
