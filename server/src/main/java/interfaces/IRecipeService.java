/*
 * 
 */
package interfaces;

import java.util.List;

import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.Recipe;
import general.RecipeSummary;


/**
 * Recipe-Interactions (import, get, get all).
 *
 * @author Daniel Langerenken
 */
public interface IRecipeService extends IGetRecipe {

  /**
   * Imports a given recipe and returns the created-id.
   *
   * @param recipe recipe to save
   * @return id of the newly created recipe
   * @throws RecipeParseException the recipe parse exception
   * @throws RecipeSavingException the recipe saving exception
   */
  String importRecipe(Recipe recipe) throws RecipeParseException, RecipeSavingException;

  /**
   * Returns a list of recipe-summaries.
   *
   * @return list of recipe-summaries (less details than whole recipes)
   * @throws RecipeParseException the recipe parse exception
   */
  List<RecipeSummary> selectRecipe() throws RecipeParseException;
}
