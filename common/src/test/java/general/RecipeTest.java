package general;

import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import utilities.DummyBuilder;
import categories.UnitTest;

/**
 * 
 * @author Patrick
 *
 *         This class provides tests to verify that the constraints in Recipe are correct
 */
public class RecipeTest {

  /**
   * Performs all tests in this class.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeRegressionTest() {
    validRecipeIsValidTest();
    emptyRecipeIsValidTest();
    recipeWithoutMashingPlanIsValidTest();
    recipeWithoutHopCookingPlanIsValidTest();
    recipeWithUnsortedMaltAdditionsIsInvalidTest();
    recipeWithUnsortedHopCookingAdditionsIsInvalidTest();
    recipeWithOverlappingTemperatureLevelsIsInvalidTest();
  }

  /**
   * Verifies that the valid recipe created by the DummyBuilder is indeed valid.
   */
  @Category(UnitTest.class)
  @Test
  public void validRecipeIsValidTest() {
    Recipe validRecipe = DummyBuilder.createValidTestRecipe();
    Assert.assertEquals(true, validRecipe.getErrorMessage() == null);
  }

  /**
   * Verifies that an empty recipe is valid. (it just does nothing)
   */
  @Category(UnitTest.class)
  @Test
  public void emptyRecipeIsValidTest() {
    Assert.assertEquals(true, new Recipe().getErrorMessage() == null);
  }

  /**
   * Verifies that the partial recipe without a mashing plan is valid.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithoutMashingPlanIsValidTest() {
    Recipe recipe = new Recipe();

    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    List<HopAddition> hopAdditions = new Vector<HopAddition>();
    hopCookingPlan.setHopAdditions(hopAdditions);

    recipe.setHopCookingPlan(hopCookingPlan);

    HopAddition ha = new HopAddition(1.0f, Unit.kg, "Hopfen 1", 2 * 1000);
    hopAdditions.add(ha);

    ha = new HopAddition(2.0f, Unit.kg, "Hopfen Zwo", 3 * 1000);
    hopAdditions.add(ha);

    ha = new HopAddition(1.5f, Unit.kg, "Hopfen 3", 6 * 1000);
    hopAdditions.add(ha);
    hopCookingPlan.setDuration(7 * 1000);

    Assert.assertEquals(true, recipe.getErrorMessage() == null);
  }

  /**
   * Verifies that the partial recipe without a hop cooking plan is valid.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithoutHopCookingPlanIsValidTest() {
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    recipe.setMashingPlan(mashingPlan);

    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(1 * 1000, 0, 50.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(1 * 1000, 1 * 1000, 60.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(2 * 1000, 2 * 1000, 75.0f);
    temperatureLevels.add(tempLevel);

    MaltAddition maltAddition;
    maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 0);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(1.0f, Unit.kg, "Malz 2", 1 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(3.5f, Unit.kg, "Malz 3", 3 * 1000);
    maltAdditions.add(maltAddition);

    Assert.assertEquals(true, recipe.getErrorMessage() == null);
  }

  /**
   * Verifies that a recipe with a mashing plan and malt additions must also have temperature.
   * levels.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithMaltAdditionButNoTemperatureLevelsIsInvalidTest() {
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    recipe.setMashingPlan(mashingPlan);

    MaltAddition maltAddition;
    maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 0);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(1.0f, Unit.kg, "Malz 2", 1 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(3.5f, Unit.kg, "Malz 3", 3 * 1000);
    maltAdditions.add(maltAddition);

    Assert.assertEquals(false, recipe.getErrorMessage() == null);
  }

  /**
   * Verifies that a recipe with a mashing plan and temperature levels must also have malt
   * additions.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithTemperatureLevelsButNoMaltAdditionsIsInvalidTest() {
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    recipe.setMashingPlan(mashingPlan);

    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(1 * 1000, 0, 50.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(1 * 1000, 1 * 1000, 60.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(2 * 1000, 2 * 1000, 75.0f);
    temperatureLevels.add(tempLevel);

    Assert.assertEquals(false, recipe.getErrorMessage() == null);
  }

  /**
   * Verifies that a recipe with malt additions that are not sorted by input time ascending will be
   * found invalid.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithUnsortedMaltAdditionsIsInvalidTest() {
    Recipe recipe = new Recipe();

    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    recipe.setMashingPlan(mashingPlan);

    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(1 * 1000, 0, 50.0f);
    temperatureLevels.add(tempLevel);

    MaltAddition maltAddition;
    maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 5 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(1.0f, Unit.kg, "Malz 2", 1 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(3.5f, Unit.kg, "Malz 3", 3 * 1000);
    maltAdditions.add(maltAddition);

    Assert.assertEquals(false, recipe.getErrorMessage() == null);
  }

  /**
   * Verifies that a recipe with hop cooking additions that are not sorted by input time ascending
   * will be found invalid.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithUnsortedHopCookingAdditionsIsInvalidTest() {
    Recipe recipe = new Recipe();

    HopCookingPlan hopCookingPlan = new HopCookingPlan();

    recipe.setHopCookingPlan(hopCookingPlan);

    HopAddition hopAddition;
    hopAddition = new HopAddition(1.0f, Unit.kg, "Hopfen1", 1000);
    hopCookingPlan.getHopAdditions().add(hopAddition);
    hopAddition = new HopAddition(1.0f, Unit.kg, "Hopfen2", 0);
    hopCookingPlan.getHopAdditions().add(hopAddition);
    hopAddition = new HopAddition(1.0f, Unit.kg, "Hopfen3", 3000);
    hopCookingPlan.getHopAdditions().add(hopAddition);

    Assert.assertTrue(recipe.getErrorMessage() != null);
  }

  /**
   * Verifies that a recipe with temperature levels that overlap will be tested as invalid.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithOverlappingTemperatureLevelsIsInvalidTest() {
    Recipe recipe = new Recipe();

    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    List<HopAddition> hopAdditions = new Vector<HopAddition>();
    hopCookingPlan.setHopAdditions(hopAdditions);

    recipe.setHopCookingPlan(hopCookingPlan);

    HopAddition ha = new HopAddition(1.0f, Unit.kg, "Hopfen 1", 2 * 1000);
    hopAdditions.add(ha);

    ha = new HopAddition(2.0f, Unit.kg, "Hopfen Zwo", 1 * 1000);
    hopAdditions.add(ha);

    Assert.assertEquals(false, recipe.getErrorMessage() == null);
  }

  /**
   * Verifies that negative hop cooking durations are invalid
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithNegativeHopCookingDurationIsInvalid() {
    Recipe recipe = new Recipe();

    HopCookingPlan hopCookingPlan = new HopCookingPlan();
    List<HopAddition> hopAdditions = new Vector<HopAddition>();
    hopCookingPlan.setHopAdditions(hopAdditions);

    recipe.setHopCookingPlan(hopCookingPlan);

    HopAddition ha = new HopAddition(1.0f, Unit.kg, "Hopfen 1", 2 * 1000);
    hopAdditions.add(ha);

    hopCookingPlan.setDuration(-1);
    Assert.assertFalse(recipe.getErrorMessage() == null);

    hopCookingPlan.setDuration(0);
    Assert.assertFalse(recipe.getErrorMessage() == null);

    hopCookingPlan.setDuration(1900);
    Assert.assertFalse(recipe.getErrorMessage() == null);

    hopCookingPlan.setDuration(2100);
    Assert.assertTrue(recipe.getErrorMessage() == null);
  }
}
