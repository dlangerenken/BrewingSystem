package general;

/**
 * MaltAddition as an ingredient for the brewing process.
 */
public class MaltAddition extends IngredientAddition {

  /**
   * constructor with all fields
   *
   * @param amount the amount of hop in "unit"
   * @param unit the unit
   * @param name the name of the hop that is to be added
   * @param inputTime the time when the hop must be inserted after the start of the hop cooking
   *        process.
   */
  public MaltAddition(float amount, Unit unit, String name, long inputTime) {
    super(amount, unit, name, inputTime);
  }

  /**
   * Empty constructor for parsing. Do NOT use this as default constructor.
   */
  @Deprecated
  public MaltAddition() {
    this(0f, Unit.kg, "", 0);
  }
}
