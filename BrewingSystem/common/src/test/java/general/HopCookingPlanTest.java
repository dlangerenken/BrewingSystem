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
 *         This class provides tests to verify that the constraints in HopCookingPlan are correct
 */
public class HopCookingPlanTest {
  /**
   * Verifies that the valid recipe created by the DummyBuilder is indeed valid.
   */
  @Category(UnitTest.class)
  @Test
  public void validRecipeHopCookingPlanIsValidTest() {
    Recipe validRecipe = DummyBuilder.createValidTestRecipe();
    HopCookingPlan plan = validRecipe.getHopCookingPlan();
    Assert.assertTrue(plan.getErrorMessage() == null);
  }

  /**
   * Verifies that an empty hop cooking plan is valid. (it just does nothing)
   */
  @Category(UnitTest.class)
  @Test
  public void emptyHopCookingPlanIsValidTest() {
    Assert.assertTrue(new HopCookingPlan().getErrorMessage() == null);
  }

  /**
   * Verifies that it is not possible to set the hop cooking additions to null.
   */
  @Category(UnitTest.class)
  @Test(expected = IllegalArgumentException.class)
  public void hopAdditionsCanNotBeSetToNullTest() {
    HopCookingPlan plan = new HopCookingPlan();
    plan.setHopAdditions(null);
  }

  /**
   * Verifies that a hop cooking plan with hop additions that are not sorted by input time
   * ascending will be tested invalid.
   */
  @Category(UnitTest.class)
  @Test
  public void hopCookingPlanWithUnsortedHopAdditionsIsInvalidTest() {
    HopCookingPlan hopCookingPlan = new HopCookingPlan();

    HopAddition hopAddition;
    hopAddition = new HopAddition(1.0f, Unit.kg, "Hopfen1", 1000);
    hopCookingPlan.getHopAdditions().add(hopAddition);
    hopAddition = new HopAddition(1.0f, Unit.kg, "Hopfen2", 0);
    hopCookingPlan.getHopAdditions().add(hopAddition);
    hopAddition = new HopAddition(1.0f, Unit.kg, "Hopfen3", 3000);
    hopCookingPlan.getHopAdditions().add(hopAddition);

    Assert.assertTrue(hopCookingPlan.getErrorMessage() != null);
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
