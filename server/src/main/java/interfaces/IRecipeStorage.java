/*
 * 
 */
package interfaces;

import java.util.List;

import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.Recipe;
import general.RecipeSummary;


/**
 * The Interface IRecipeStorage.
 */
public interface IRecipeStorage {
  
  /**
   * Gets the recipe.
   *
   * @param id the id
   * @return the recipe
   * @throws RecipeNotFoundException the recipe not found exception
   * @throws RecipeParseException the recipe parse exception
   */
  Recipe getRecipe(final String id) throws RecipeNotFoundException, RecipeParseException;

  /**
   * Gets the recipe summaries.
   *
   * @return the recipe summaries
   * @throws RecipeParseException the recipe parse exception
   */
  List<RecipeSummary> getRecipeSummaries() throws RecipeParseException;

  /**
   * Save recipe.
   *
   * @param recipe the recipe
   * @return the string
   * @throws RecipeParseException the recipe parse exception
   * @throws RecipeSavingException the recipe saving exception
   */
  String saveRecipe(Recipe recipe) throws RecipeParseException, RecipeSavingException;
}
