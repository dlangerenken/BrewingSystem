/*
 * 
 */
package parser;


/**
 * Constants for the recipe-parsing/reading.
 *
 * @author Daniel Langerenken
 */
public final class RecipeConstants {

  /** Name for the recipe-Node inside of the XML-Document. */
  public final static String RECIPE_NODE = "recipe";

  /** Name for the recipe-summary-Node inside of the XML-Document. */
  public final static String RECIPE_SUMMARY = "summary";

  /** Name for the name-Node inside of the XML-Document. */
  public final static String NAME_NODE = "name";

  /** Name for the description-Node inside of the XML-Document. */
  public final static String DESCRIPTION_NODE = "desc";

  /** Name for the id-Node inside of the XML-Document. */
  public final static String ID_NODE = "id";

  /** Name for the mashplan-Node inside of the XML-Document. */
  public final static String MASHPLAN_NODE = "mashplan";

  /** Name for the temperature-levels-Node inside of the XML-Document. */
  public final static String TEMP_LEVEL_NODE = "templevel";

  /** Name for the temp-level-Node inside of the XML-Document. */
  public final static String LEVEL_NODE = "level";

  /** Name for the start-time-Node inside of the XML-Document. */
  public final static String START_TIME_NODE = "start";

  /** Name for the duration-Node inside of the XML-Document. */
  public final static String DURATION_NODE = "duration";

  /** Name for the temp-Node inside of the XML-Document. */
  public final static String TEMP_NODE = "temp";

  /** Name for the malt-addition-Node inside of the XML-Document. */
  public final static String MALT_ADDITION_NODE = "maltaddition";

  /** Name for the malt-Node inside of the XML-Document. */
  public final static String MALT_NODE = "malt";

  /** Name for the amount-Node inside of the XML-Document. */
  public final static String AMOUNT_NODE = "amount";

  /** Name for the unit-Node inside of the XML-Document. */
  public final static String UNIT_NODE = "unit";

  /** Name for the input-time-Node inside of the XML-Document. */
  public final static String INPUT_TIME_NODE = "input";

  /** Name for the hop-additions-Node inside of the XML-Document. */
  public final static String HOP_ADDITION_NODE = "hopaddition";

  /** Name for the hop-Node inside of the XML-Document. */
  public final static String HOP_NODE = "hop";

  /** Name for the manual-steps-Node inside of the XML-Document. */
  public final static String MANUAL_STEPS_NODE = "manualsteps";

  /** Name for the manual-Node inside of the XML-Document. */
  public final static String MANUAL_NODE = "manual";

  /** Name for the manual-Node inside of the XML-Document. */
  public final static String DATE_NODE = "date";

  /** Name for the NULL-Node for Objects which are not specified. */
  public final static String NULL_NODE = "[!NULL!]";

  /**
   * Does nothing.
   *
   * @throws IllegalAccessException the illegal access exception
   */
  private RecipeConstants() throws IllegalAccessException {
    throw new IllegalAccessException("Not allowed to instantiate this class");
  }
}
