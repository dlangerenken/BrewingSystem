/*
 * 
 */
package network;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import dispatcher.Context;
import dispatcher.HttpError;
import dispatcher.Result;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.Recipe;
import general.RecipeSummary;
import gson.Serializer;


/**
 * This class deals with everything about the recipe-management (create recipe, show recipe, show
 * recipes).
 *
 * @author Daniel Langerenken
 */
public class RecipeResource extends RestResource<RecipeSummary, Recipe, String> {

  /**
   * Creates the recipe resource.
   *
   * @param context Context of the servlet
   */
  public RecipeResource(final Context context) {
    super(context);
  }

  /**
   * Creates the recipe resource.
   *
   * @param context Context of the servlet
   * @param id Id of the single recipe
   */
  public RecipeResource(final Context context, final String id) {
    super(context, id);
  }

  @Override
  public JsonResult<List<RecipeSummary>> all() throws HttpError {
    try {
      Type listType = new TypeToken<List<RecipeSummary>>() {
        /* Needs to be an empty block */
      }.getType();
      return new JsonResult<List<RecipeSummary>>(getUserFacade().selectRecipe(), listType);
    } catch (RecipeParseException e) {
      returnServerException(e);
    }
    return null;
  }

  /**
   * Creates a recipe with the given parameters.
   *
   * @param recipeString the recipe string
   * @return Id of the recipe
   * @throws HttpError If recipe was invalid
   */
  public Result createRecipe(final String recipeString) throws HttpError {
    LOGGER.info("Trying to create recipe from string: " + recipeString);
    Recipe recipe = Serializer.getInstance().fromJson(recipeString, Recipe.class);
    try {
      return new JsonResult<String>(getUserFacade().importRecipe(recipe), String.class);
    } catch (RecipeParseException e) {
      returnServerException(e);
    } catch (RecipeSavingException e) {
      returnBadRequestException(e);
    }
    return null;
  }

  @Override
  public JsonResult<Recipe> handleSingle() throws HttpError {
    try {
      return new JsonResult<Recipe>(getUserFacade().getRecipe(getId()), Recipe.class);
    } catch (RecipeParseException e) {
      returnServerException(e);
    } catch (RecipeNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

}
