package network;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static utilities.NetworkRequestHelper.sendGet;
import static utilities.NetworkRequestHelper.sendPost;

import java.net.URLEncoder;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;
import utilities.DummyBuilder;
import utilities.NetworkResult;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import general.Recipe;
import gson.Serializer;

/**
 * This class provides tests for the recipe resource and checks that the network-request is
 * reassigned correctly
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RecipeResourceTest extends ResourceControllerTestHelper {
  /**
   * Checks if /recipes/create?recipe=gson(recipe) is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testCreateRecipe() throws Exception {
    Recipe recipe = DummyBuilder.getRecipe();
    sendPost(serverAddress + "recipes/create", "recipe=" + Serializer.getInstance().toJson(recipe));
    verify(userFacade, timeout(300).times(1)).importRecipe(recipe);
  }

  /**
   * Checks if /recipes/XY/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipe() throws Exception {
    sendGet(serverAddress + "recipes/1/");
    verify(userFacade, timeout(300).times(1)).getRecipe("1");
  }
  
  /**
   * Checks if /recipes/äöüß/ is valid
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testUmlaute() throws Exception{
    sendGet(serverAddress + "recipes/" +  URLEncoder.encode("äöüß", "UTF-8") + "/");
    verify(userFacade, timeout(300).times(1)).getRecipe("äöüß");
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeParsingException() throws Exception {
    Mockito.doThrow(RecipeParseException.class).when(userFacade).getRecipe("1");
    NetworkResult result = sendGet(serverAddress + "recipes/1/");
    verify(userFacade, timeout(300).times(1)).getRecipe("1");
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if /recipes/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipes() throws Exception {
    sendGet(serverAddress + "recipes/");
    verify(userFacade, timeout(300).times(1)).selectRecipe();
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipesParsingException() throws Exception {
    Mockito.doThrow(RecipeParseException.class).when(userFacade).selectRecipe();
    NetworkResult result = sendGet(serverAddress + "recipes/");
    verify(userFacade, timeout(300).times(1)).selectRecipe();
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeThrowsNotFoundException() throws Exception {
    Mockito.doThrow(RecipeNotFoundException.class).when(userFacade).getRecipe("1");
    NetworkResult result = sendGet(serverAddress + "recipes/1/");
    verify(userFacade, timeout(300).times(1)).getRecipe("1");
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

}
