/*
 *
 */
package general;

import interfaces.IObjectWithValidationStatus;

import java.io.Serializable;


/**
 * A recipe for a certain beer, including the entire data needed for brewing (hop cooking plan,
 * mashing plan etc.).
 */
public class Recipe implements Serializable, IObjectWithValidationStatus {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -673708880032516767L;

  /** The name of the beer. */
  private String name;

  /** The description of the beer and/or recipe. */
  private String description;

  /**
   * The id of beer, will also serve as filename of the recipe file. PersistenceHandler updates this
   * if file with same name already exists when it is saved.
   */
  private String id;

  /** The date this recipe was created. */
  private long date;

  /** The mashing plan. */
  private MashingPlan mashingPlan;

  /** The hop cooking plan. */
  private HopCookingPlan hopCookingPlan;

  /** empty constructor */
  public Recipe() {};

  /** constructor with all fields */
  public Recipe(final String id, final String name, final String desc, final long date,
      final MashingPlan mashingPlan, final HopCookingPlan hopCookingPlan) {
    this.id = id;
    this.name = name;
    description = desc;
    this.date = date;
    this.mashingPlan = mashingPlan;
    this.hopCookingPlan = hopCookingPlan;
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
    Recipe other = (Recipe) obj;
    if (date != other.date) {
      return false;
    }
    if (hopCookingPlan == null) {
      if (other.hopCookingPlan != null) {
        return false;
      }
    } else if (!hopCookingPlan.equals(other.hopCookingPlan)) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (mashingPlan == null) {
      if (other.mashingPlan != null) {
        return false;
      }
    } else if (!mashingPlan.equals(other.mashingPlan)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the date this recipe was created.
   *
   * @return the date
   */
  public long getDate() {
    return date;
  }

  /**
   * Gets the description of the beer/recipe.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * A recipe is invalid if: There is no mashing plan There is no hop cooking plan The mashing plan
   * is invalid The hop cooking plan is invalid e.g. they overlap
   *
   * @return a string containing an error message if the recipe is not valid and null else wise
   */
  @Override
  public String getErrorMessage() {
    final StringBuilder errorMessage = new StringBuilder();
    if (mashingPlan != null) {
      final String mashingError = mashingPlan.getErrorMessage();
      if (mashingError != null) {
        errorMessage.append(mashingError);
      }
    }
    if (hopCookingPlan != null) {
      final String hopCookingError = hopCookingPlan.getErrorMessage();
      if (hopCookingError != null) {
        errorMessage.append(hopCookingError);
      }
    }

    final String error = errorMessage.toString();
    return error.isEmpty() ? null : error;
  }

  /**
   * Whether or not the recipe is valid
   * 
   * @return true, if valid, false otherwise
   */
  public boolean isValid() {
    return getErrorMessage() == null;
  }

  /**
   * Gets the hop cooking plan.
   *
   * @return the hop cooking plan
   */
  public HopCookingPlan getHopCookingPlan() {
    return hopCookingPlan;
  }

  /**
   * Gets the id of this recipe.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the mashing plan.
   *
   * @return the mashing plan
   */
  public MashingPlan getMashingPlan() {
    return mashingPlan;
  }

  /**
   * Gets the name of the beer.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets a summary of the recipe, for user selection.
   *
   * @return the summary
   */
  public RecipeSummary getSummary() {
    return new RecipeSummary(name, description, date, id);
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (date ^ (date >>> 32));
    result = prime * result + ((hopCookingPlan == null) ? 0 : hopCookingPlan.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((mashingPlan == null) ? 0 : mashingPlan.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /**
   * Sets the date.
   *
   * @param date the new date
   */
  public void setDate(final long date) {
    this.date = date;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * Sets the hop cooking plan.
   *
   * @param hopCookingPlan the new hop cooking plan
   */
  public void setHopCookingPlan(final HopCookingPlan hopCookingPlan) {
    this.hopCookingPlan = hopCookingPlan;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Sets the mashing plan.
   *
   * @param mashingPlan the new mashing plan
   */
  public void setMashingPlan(final MashingPlan mashingPlan) {
    this.mashingPlan = mashingPlan;
  }

  /**
   * Sets the name of the beer.
   *
   * @param name the new name
   */
  public void setName(final String name) {
    this.name = name;
  }
}
