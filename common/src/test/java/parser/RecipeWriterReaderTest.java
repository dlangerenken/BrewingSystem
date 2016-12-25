/*
 * 
 */
package parser;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import utilities.DummyBuilder;
import categories.UnitTest;
import exceptions.RecipeParseException;
import general.HopAddition;
import general.HopCookingPlan;
import general.MaltAddition;
import general.MashingPlan;
import general.Recipe;
import general.RecipeSummary;


/**
 * Tests the whole recipe writer/reader classes.
 *
 * @author Daniel Langerenken
 */
public class RecipeWriterReaderTest {

  /** The writer which is used to write recipes. */
  private RecipeWriter writer;

  /** The reader which is used to read files and converts them to recipes. */
  private RecipeReader reader;

  /**
   * Assert equals null or empty as xml does not differ between "null" and "empty".
   *
   * @param first the first string which can be null or empty
   * @param second the second string which can be null or empty
   */
  public void assertEqualsNullOrEmpty(final String first, final String second) {
    if (isNullOrEmpty(first)) {
      Assert.assertTrue(isNullOrEmpty(second));
    } else {
      Assert.assertEquals(first, second);
    }
  }

  /**
   * Inits the writer and reader for every single test
   */
  @Before
  public void init() {
    writer = new RecipeWriter();
    reader = new RecipeReader();
  }

  /**
   * Checks if a string is null or empty.
   *
   * @param string the string which should be checked
   * @return true, if the string is null or empty
   */
  private boolean isNullOrEmpty(final String string) {
    return string == null || string.equals("");
  }

  /**
   * Test the exception management of reader and writer classes.
   *
   * @throws RecipeParseException the recipe parse exception which hopefully does not occur
   */
  @Category(UnitTest.class)
  @Test
  public void testExceptionManagement() throws RecipeParseException {
    Recipe incompleteRecipe = new Recipe();
    testRecipe(incompleteRecipe);

    incompleteRecipe.setId("1337");
    testRecipe(incompleteRecipe);

    HopCookingPlan plan = new HopCookingPlan();
    incompleteRecipe.setHopCookingPlan(plan);
    testRecipe(incompleteRecipe);

    plan.setHopAdditions(new ArrayList<HopAddition>());
    plan.getHopAdditions().add(DummyBuilder.getHopAddition());
    testRecipe(incompleteRecipe);

    MashingPlan mashPlan = new MashingPlan();
    incompleteRecipe.setMashingPlan(mashPlan);
    testRecipe(incompleteRecipe);

    mashPlan.setMaltAdditions(new ArrayList<MaltAddition>());
    mashPlan.getMaltAdditions().add(DummyBuilder.getMaltAddition());
    testRecipe(incompleteRecipe);
  }

  /**
   * Test the writing and reading of a recipe.
   *
   * @param myRecipe the recipe which should be parsed
   * @throws RecipeParseException the recipe parse exception which should not occur
   */
  private void testRecipe(final Recipe myRecipe) throws RecipeParseException {
    String recipeString = writer.convertRecipeToXml(myRecipe);
    RecipeSummary recipeSummary = reader.getRecipeSummaryByString(recipeString);
    Recipe recipe = reader.getRecipeByString(recipeString);
    recipeSummary.setId(myRecipe.getId());
    recipe.setId(myRecipe.getId());
    testRecipe(recipe, myRecipe);
    testRecipeWithSummary(myRecipe, recipeSummary);
  }

  /**
   * Checks if two recipes are equal
   *
   * @param firstRecipe the first recipe
   * @param secondRecipe the second recipe
   */
  private void testRecipe(final Recipe firstRecipe, final Recipe secondRecipe) {
    Assert.assertEquals(firstRecipe, secondRecipe);
  }

  /**
   * Checks if a recipe-summary contains the same values for shared properties as the given recipe.
   *
   * @param myRecipe the recipe which contains a summary
   * @param summary the summary of the recipe
   */
  private void testRecipeWithSummary(final Recipe recipe, final RecipeSummary summary) {
    Assert.assertEquals(recipe.getDate(), summary.getDate());
    assertEqualsNullOrEmpty(recipe.getDescription(), summary.getDescription());
    assertEqualsNullOrEmpty(recipe.getName(), summary.getTitle());
    assertEqualsNullOrEmpty(recipe.getId(), summary.getId());
  }

  /**
   * Single test with complex recipe which is translated to a xml-file and then parsed back to a
   * recipe
   * 
   * @throws RecipeParseException Thrown if parsing / converting failed
   */
  @Category(UnitTest.class)
  @Test
  public void testSingleRecipe() throws RecipeParseException {
    Recipe myRecipe = DummyBuilder.getRecipe();
    testRecipe(myRecipe);
  }

  /**
   * Tests if the duration of a hop cooking is now included in a recipe-file during import and
   * export
   * 
   * @throws RecipeParseException
   */
  @Category(UnitTest.class)
  @Test
  public void testIssue159MissingDuration() throws RecipeParseException {
    Recipe myRecipe = DummyBuilder.getRecipe();
    myRecipe.getHopCookingPlan().setDuration(1337);
    testRecipe(myRecipe);
  }

}
