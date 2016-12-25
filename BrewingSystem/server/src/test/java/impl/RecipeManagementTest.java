/*
 * 
 */
package impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import exceptions.BrewingProcessException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.Recipe;
import general.RecipeSummary;
import interfaces.IRecipeService;
import interfaces.IRecipeStorage;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.DummyBuilder;
import categories.UnitTest;


/**
 * This class tests the redirecting of the user-facade (in case something changes inside of the
 * user-facade.
 *
 * @author Daniel Langerenken
 */
@RunWith(MockitoJUnitRunner.class)
public class RecipeManagementTest {

  /**
   * Creates an expected exception to check if exceptions are thrown within the tests
   */
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  /** The recipe. */
  private Recipe recipe;

  /** The recipe service. */
  private IRecipeService recipeService;

  /** The recipe storage. */
  private IRecipeStorage recipeStorage;

  /**
   * Inits the recipestorage, service and recipe for every single test.
   */
  @Before
  public void init() {
    recipeStorage = mock(IRecipeStorage.class);
    recipeService = new RecipeManagement(recipeStorage);
    recipe = DummyBuilder.getRealisticRecipe();
  }

  /**
   * Tests if a recipe is correctly received from the recipeStorage
   * 
   * @throws RecipeParseException
   * @throws RecipeNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void testGetRecipe() throws RecipeNotFoundException, RecipeParseException {
    when(recipeStorage.getRecipe("42")).thenReturn(recipe);
    Recipe mRecipe = recipeService.getRecipe("42");
    verify(recipeStorage).getRecipe("42");
    Assert.assertEquals(mRecipe, recipe);
  }

  /**
   * Tests the redirection of calls to the appropriate services.
   *
   * @throws BrewingProcessException the brewing process exception (should not occur in this test)
   * @throws RecipeParseException the recipe parse exception (should not occur in this test)
   * @throws RecipeSavingException the recipe saving exception (should not occur in this test)
   */
  @Category(UnitTest.class)
  @Test
  public void testImportRecipe() throws BrewingProcessException, RecipeParseException,
      RecipeSavingException {
    when(recipeStorage.saveRecipe(recipe)).thenReturn("1337");
    String id = recipeService.importRecipe(recipe);
    verify(recipeStorage).saveRecipe(recipe);
    Assert.assertEquals(id, "1337");
  }

  /**
   * Test to receive a not valid recipe - just redirection.
   *
   * @throws RecipeNotFoundException the recipe not found exception which should be thrown
   * @throws RecipeParseException the recipe parse exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeNotFoundExceptionThrown() throws RecipeNotFoundException,
      RecipeParseException {
    when(recipeStorage.getRecipe("1337")).thenThrow(new RecipeNotFoundException());
    expectedException.expect(RecipeNotFoundException.class);
    recipeService.getRecipe("1337");
    verify(recipeStorage).getRecipe("1337");
  }


  /**
   * Test to receive a not valid recipe - just redirection.
   *
   * @throws RecipeNotFoundException the recipe not found exception which should be thrown
   * @throws RecipeParseException the recipe parse exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeParseExceptionThrown() throws RecipeNotFoundException, RecipeParseException {
    when(recipeStorage.getRecipe("1337")).thenThrow(new RecipeParseException(""));
    expectedException.expect(RecipeParseException.class);
    recipeService.getRecipe("1337");
    verify(recipeStorage).getRecipe("1337");
  }

  /**
   * Tests select recipe
   */
  @Category(UnitTest.class)
  @Test
  public void testSelectRecipe() throws RecipeParseException {
    List<RecipeSummary> recipeSummaries = new ArrayList<RecipeSummary>();
    recipeSummaries.add(mock(RecipeSummary.class));
    when(recipeService.selectRecipe()).thenReturn(recipeSummaries);
    List<RecipeSummary> recipes = recipeService.selectRecipe();
    verify(recipeStorage).getRecipeSummaries();
    Assert.assertEquals(recipeSummaries, recipes);
  }

}
