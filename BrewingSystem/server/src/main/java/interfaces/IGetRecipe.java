/*
 * 
 */
package interfaces;

import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import general.Recipe;
import general.RecipeSummary;


/**
 * Interface which provides the get-recipe-methods.
 *
 * @author Daniel Langerenken
 */
public interface IGetRecipe {
  
  /**
   * Returns the recipe with the given id.
   *
   * @param summary summary of the recipe
   * @return recipe with the given summary
   * @throws RecipeNotFoundException thrown, if no recipe with the given id is found
   * @throws RecipeParseException the recipe parse exception
   */
  Recipe getRecipe(RecipeSummary summary) throws RecipeNotFoundException, RecipeParseException;

  /**
   * Returns the recipe with the given id.
   *
   * @param id id of the recipe
   * @return recipe with the given id
   * @throws RecipeNotFoundException thrown, if no recipe with the given id is found
   * @throws RecipeParseException the recipe parse exception
   */
  Recipe getRecipe(String id) throws RecipeNotFoundException, RecipeParseException;
}
