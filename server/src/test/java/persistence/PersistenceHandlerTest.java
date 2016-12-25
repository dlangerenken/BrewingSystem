/*
 *
 */

package persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.DummyBuilder;
import utilities.PropertyUtil;
import categories.UnitTest;
import exceptions.LogNotFoundException;
import exceptions.LogParseException;
import exceptions.LogSavingException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.BrewingLog;
import general.Recipe;
import general.RecipeSummary;


/**
 * This class tests the persistence handler.
 *
 * @author matthias
 */
@RunWith(MockitoJUnitRunner.class)
public class PersistenceHandlerTest {

  /** The persistance handler. */
  private static PersistenceHandler persistenceHandler;

  /**
   * Removes all files in the testRecipes and testLogs folders and the folders themselves
   */
  @AfterClass
  public static void cleanup() {
    File recipeFolder = new File(PropertyUtil.PROJECT_FOLDER_PATH + File.separator + "testRecipes");
    File logFolder = new File(PropertyUtil.PROJECT_FOLDER_PATH + File.separator + "testLogs");
    for (File f : logFolder.listFiles()) {
      f.delete();
    }
    for (File f : recipeFolder.listFiles()) {
      f.delete();
    }
    recipeFolder.delete();
    logFolder.delete();
    persistenceHandler.setLogPath(PropertyUtil.LOG_PATH);
    persistenceHandler.setRecipePath(PropertyUtil.RECIPE_PATH);
  }


  /**
   * Initializes the objects required for testing.
   */
  @BeforeClass
  public static void init() {
    persistenceHandler = new PersistenceHandler();
    persistenceHandler.setLogPath(PropertyUtil.PROJECT_FOLDER_PATH + File.separator + "testLogs");
    persistenceHandler.setRecipePath(PropertyUtil.PROJECT_FOLDER_PATH + File.separator
        + "testRecipes");
  }

  /**
   * Tests that an empty recipe directory is handled correctly.
   *
   * @throws RecipeParseException
   */
  @Category(UnitTest.class)
  @Test
  public void testEmptyRecipeDirectory() throws RecipeParseException {
    File recipeFolder = new File(PropertyUtil.PROJECT_FOLDER_PATH + File.separator + "testRecipes");
    File[] files = recipeFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    Assert.assertEquals(persistenceHandler.getRecipeSummaries(), new ArrayList<RecipeSummary>());
  }

  /**
   * Tests that file names are numbered if file with same name is already present. Saves two files
   * with same name and checks that both are saved with unique names but are equal if the numbering
   * is removed.
   *
   * @throws LogSavingException
   */
  @Category(UnitTest.class)
  @Test
  public void testFileNumbering() throws LogSavingException {
    BrewingLog log1 = DummyBuilder.getBrewingLog();
    File file1 = persistenceHandler.saveLog(log1);
    File file2 = persistenceHandler.saveLog(log1);
    Assert.assertFalse(file1.getPath().equals(file2.getPath()));
    Assert.assertTrue(file1.delete());
    Assert.assertTrue(file2.delete());
  }

  /**
   * Tests that LogParseException is thrown
   *
   * @throws LogNotFoundException
   * @throws LogParseException
   */
  @Category(UnitTest.class)
  @Test(expected = LogParseException.class)
  public void testInvalidStringLog() throws LogNotFoundException, LogParseException {
    persistenceHandler.getLogByString("int main(int argc, char** argv) {return 0;}");
  }

  /**
   * Test log handling.
   *
   * @throws LogSavingException the log saving exception
   * @throws LogParseException
   * @throws LogNotFoundException
   */
  @Category(UnitTest.class)
  @Test
  public void testLogHandling() throws LogSavingException, LogNotFoundException, LogParseException {
    BrewingLog log = DummyBuilder.getBrewingLog();
    int id = log.getId();
    String logPath = persistenceHandler.getLogPath();
    long updateTime = persistenceHandler.getLastLogUpdateTime();
    File file = persistenceHandler.saveLog(log);
    Assert.assertTrue(persistenceHandler.getLastLogUpdateTime() > updateTime);
    Assert.assertTrue((new Date()).getTime() >= persistenceHandler.getLastLogUpdateTime());
    Assert.assertTrue(file.getPath().startsWith(logPath));
    Assert.assertTrue(file.exists());
    BrewingLog parsedLog = persistenceHandler.getLogById(id);
    Assert.assertEquals(log, parsedLog);
    List<BrewingLog> logs = persistenceHandler.getLogs();
    Assert.assertTrue(logs.contains(log));
    Assert.assertTrue(file.delete());
  }



  /**
   * Tests that {@link LogNotFoundException} is thrown
   *
   * @throws LogNotFoundException
   * @throws LogParseException
   */
  @Category(UnitTest.class)
  @Test(expected = LogNotFoundException.class)
  public void testLogNotFound() throws LogNotFoundException, LogParseException {
    persistenceHandler.getLogById(-1);
  }

  /**
   * Tests that saving recipes with same id works.
   *
   * @throws RecipeSavingException
   * @throws RecipeParseException
   * @throws LogSavingException
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeDuplicate() throws RecipeParseException, RecipeSavingException {
    Recipe recipe = DummyBuilder.getRealisticRecipe();
    Recipe recipe2 = DummyBuilder.getRealisticRecipe();
    Assert.assertEquals(recipe.getId(), recipe2.getId());
    persistenceHandler.saveRecipe(recipe);
    persistenceHandler.saveRecipe(recipe2);
    Assert.assertFalse(recipe.getId() == recipe2.getId());
  }

  /**
   * Test recipe handling.
   *
   * @throws RecipeParseException the recipe parse exception
   * @throws RecipeSavingException the recipe saving exception
   * @throws RecipeNotFoundException the recipe not found exception
   */
  @Category(UnitTest.class)
  @Test
  public void testRecipeHandling() throws RecipeParseException, RecipeSavingException,
      RecipeNotFoundException {
    persistenceHandler.setRecipePath(PropertyUtil.PROJECT_FOLDER_PATH + File.separator
        + "testRecipes");
    Recipe recipe = DummyBuilder.getRecipe();
    recipe.setId("testID");
    String id = persistenceHandler.saveRecipe(recipe);
    Assert.assertEquals(id, recipe.getId());
    List<RecipeSummary> index = persistenceHandler.getRecipeSummaries();
    boolean contains = false;
    for (RecipeSummary summary : index) {
      contains = contains || (summary.getId().equals(id));
    }
    Assert.assertTrue(contains);
    Recipe loadedRecipe = persistenceHandler.getRecipe(recipe.getId());
    Assert.assertEquals(recipe, loadedRecipe);
    File file =
        new File(persistenceHandler.getRecipePath() + File.separator + id
            + PropertyUtil.RECIPE_FILE_EXT);
    Assert.assertTrue(file.exists());
    Assert.assertTrue(file.delete());
  }
}
