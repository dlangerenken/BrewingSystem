/*
 * 
 */
package general;


/**
 * Ingredient for the brewing process.
 */
public class Ingredient {

  /** The amount of the ingredient which should be added. */
  private float amount;

  /**
   * The unit of the amount (e.g. kg)
   */
  private Unit unit;

  /** The name of the ingredient. */
  private String name;

  /**
   * Creates an ingredient with a given amount of a unit and a name
   * 
   * @param amount
   * @param unit
   * @param name
   */
  public Ingredient(final float amount, final Unit unit, final String name) {
    this.amount = amount;
    this.unit = unit;
    this.name = name;
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
    Ingredient other = (Ingredient) obj;
    if (Float.floatToIntBits(amount) != Float.floatToIntBits(other.amount)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (unit != other.unit) {
      return false;
    }
    return true;
  }

  /**
   * Amount of the ingredient.
   *
   * @return amount
   */
  public float getAmount() {
    return amount;
  }

  /**
   * Returns the name of the ingredient.
   *
   * @return name of the ingredient
   */
  public String getName() {
    return name;
  }

  /**
   * Unit of the amount.
   *
   * @return enum-type (e.g. g for gram)
   */
  public Unit getUnit() {
    return unit;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(amount);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((unit == null) ? 0 : unit.hashCode());
    return result;
  }

  /**
   * Sets the new amount of the ingredient.
   *
   * @param amount - new amount (take care of the unit)
   */
  public void setAmount(final float amount) {
    this.amount = amount;
  }

  /**
   * Sets the name of the ingredient.
   *
   * @param name - the name of the ingredient
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Sets the new unit for the ingredient.
   *
   * @param unit new unit (e.g. g) take care of the amount
   */
  public void setUnit(final Unit unit) {
    this.unit = unit;
  }
}
