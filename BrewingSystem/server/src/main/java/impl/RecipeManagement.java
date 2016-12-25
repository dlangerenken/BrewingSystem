/*
 * 
 */
package impl;

import general.Recipe;
import general.RecipeSummary;
import interfaces.IRecipeService;
import interfaces.IRecipeStorage;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;


/**
 * This class handles all interactions with recipes (get recipe, get recipe-list, create recipe).
 *
 * @author Daniel Langerenken
 */
@Singleton
public class RecipeManagement implements IRecipeService {

  /** Global Logger for logging interactions with the recipe-management. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Some "storage" where the recipe management is getting the recipes from and imports the recipes
   * to.
   */
  private final IRecipeStorage recipeStorage;

  /**
   * Instantiates the recipe-management with a given storage-location.
   *
   * @param recipeStorage - Storage where to get recipes from and put recipes in
   */
  @Inject
  public RecipeManagement(final IRecipeStorage recipeStorage) {
    this.recipeStorage = recipeStorage;
    LOGGER.info("RecipeManagement constructed");
  }

  @Override
  public Recipe getRecipe(final RecipeSummary summary) throws RecipeNotFoundException,
      RecipeParseException {
    Recipe recipe = getRecipe(summary.getId());
    if (recipe.isValid()) {
      return recipe;
    }
    throw new RecipeParseException(String.format("Recipe is not valid: %s",
        recipe.getErrorMessage()));
  }

  @Override
  public Recipe getRecipe(final String id) throws RecipeNotFoundException, RecipeParseException {
    Recipe recipe = recipeStorage.getRecipe(id);
    if (recipe != null && recipe.isValid()) {
      return recipe;
    }
    if (recipe == null) {
      throw new RecipeNotFoundException();
    }
    throw new RecipeParseException(String.format("Recipe is not valid: %s",
        recipe.getErrorMessage()));
  }

  @Override
  public String importRecipe(final Recipe recipe) throws RecipeParseException,
      RecipeSavingException {
    String errorMessage = recipe.getErrorMessage();
    if (errorMessage != null) {
      throw new RecipeSavingException(String.format("The imported recipe is not valid: %s",
          errorMessage));
    }
    String id = null;
    id = recipeStorage.saveRecipe(recipe);
    recipe.setId(id);
    LOGGER.info("Recipe imported: " + id);
    return id;
  }

  @Override
  public List<RecipeSummary> selectRecipe() throws RecipeParseException {
    List<RecipeSummary> recipes = null;
    recipes = recipeStorage.getRecipeSummaries();
    return recipes;
  }

}
