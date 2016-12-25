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
 *         This class provides tests to verify that the constraints in MashingPlan are correct
 */
public class MashingPlanTest {
  /**
   * Verifies that the valid recipe created by the DummyBuilder is indeed valid.
   */
  @Category(UnitTest.class)
  @Test
  public void validRecipeMashingPlanIsValidTest() {
    Recipe validRecipe = DummyBuilder.createValidTestRecipe();
    MashingPlan plan = validRecipe.getMashingPlan();
    Assert.assertTrue(plan.getErrorMessage() == null);
  }

  /**
   * Verifies that an empty mashing plan is valid. (it just does nothing)
   */
  @Category(UnitTest.class)
  @Test
  public void emptyMashingPlanIsValidTest() {
    Assert.assertTrue(new MashingPlan().getErrorMessage() == null);
  }

  /**
   * Verifies a mashing plan with malt additions must also have temperature levels.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithMaltAdditionButNoTemperatureLevelsIsInvalidTest() {
    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    MaltAddition maltAddition;
    maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 0);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(1.0f, Unit.kg, "Malz 2", 1 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(3.5f, Unit.kg, "Malz 3", 3 * 1000);
    maltAdditions.add(maltAddition);

    Assert.assertTrue(mashingPlan.getErrorMessage() != null);
  }

  /**
   * Verifies that a mashing plan with temperature levels must also have malt additions.
   */
  @Category(UnitTest.class)
  @Test
  public void recipeWithTemperatureLevelsButNoMaltAdditionsIsInvalidTest() {
    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(1 * 1000, 0, 50.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(1 * 1000, 1 * 1000, 60.0f);
    temperatureLevels.add(tempLevel);
    tempLevel = new TemperatureLevel(2 * 1000, 2 * 1000, 75.0f);
    temperatureLevels.add(tempLevel);

    Assert.assertTrue(mashingPlan.getErrorMessage() != null);
  }

  /**
   * Verifies that it is not possible to set the temperature levels to null.
   */
  @Category(UnitTest.class)
  @Test(expected = IllegalArgumentException.class)
  public void temperatureLevelsCanNotBeSetToNullTest() {
    MashingPlan plan = new MashingPlan();
    plan.setTemperatureLevels(null);
  }

  /**
   * Verifies that it is not possible to set the malt additions to null.
   */
  @Category(UnitTest.class)
  @Test(expected = IllegalArgumentException.class)
  public void maltAdditoonsCanNotBeSetToNullTest() {
    MashingPlan plan = new MashingPlan();
    plan.setMaltAdditions(null);
  }

  /**
   * Verifies that a mashing plan with malt additions that are not sorted by input time ascending
   * will be
   * found invalid.
   */
  @Category(UnitTest.class)
  @Test
  public void mashingPlanWithUnsortedMaltAdditionsIsInvalidTest() {
    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    List<MaltAddition> maltAdditions = new Vector<MaltAddition>();
    mashingPlan.setTemperatureLevels(temperatureLevels);
    mashingPlan.setMaltAdditions(maltAdditions);

    MaltAddition maltAddition;
    maltAddition = new MaltAddition(2.0f, Unit.kg, "Malz 1", 5 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(1.0f, Unit.kg, "Malz 2", 1 * 1000);
    maltAdditions.add(maltAddition);
    maltAddition = new MaltAddition(3.5f, Unit.kg, "Malz 3", 3 * 1000);
    maltAdditions.add(maltAddition);

    Assert.assertTrue(mashingPlan.getErrorMessage() != null);
  }

  /**
   * Verifies that a mashing plan with temperature levels that overlap will be tested as invalid.
   */
  @Category(UnitTest.class)
  @Test
  public void mashingPlanWithOverlappingTemperatureLevelsIsInvalidTest() {
    MashingPlan mashingPlan = new MashingPlan();
    List<TemperatureLevel> temperatureLevels = new Vector<TemperatureLevel>();
    mashingPlan.setTemperatureLevels(temperatureLevels);

    TemperatureLevel tempLevel;
    tempLevel = new TemperatureLevel(1000, 2, 1.0f);
    temperatureLevels.add(tempLevel);

    tempLevel = new TemperatureLevel(1000, 2500, 1.0f);
    temperatureLevels.add(tempLevel);

    tempLevel = new TemperatureLevel(1000, 0, 1.0f);
    temperatureLevels.add(tempLevel);

    Assert.assertTrue(mashingPlan.getErrorMessage() != null);

  }
}
