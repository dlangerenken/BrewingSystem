/*
 *
 */
package persistence;

import exceptions.FileCreationException;
import exceptions.LogNotFoundException;
import exceptions.LogParseException;
import exceptions.LogSavingException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;
import exceptions.RecipeSavingException;
import general.BrewingLog;
import general.Recipe;
import general.RecipeSummary;
import gson.Serializer;
import interfaces.ILogStorage;
import interfaces.IRecipeStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import parser.RecipeReader;
import parser.RecipeWriter;
import utilities.PropertyUtil;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Singleton;

/**
 * This class handles the storing and loading of recipes, protocols, logs and summaries.
 *
 * @author matthias
 */
@Singleton
public class PersistenceHandler implements ILogStorage, IRecipeStorage {

  /** global logger object */
  public static final Logger LOGGER = LogManager.getLogger();

  /** The recipe reader, creates recipes from xml files. */
  private final RecipeReader reader = new RecipeReader();

  /** formats date for use in filenames */
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss");

  /** The main class for using gson */
  private final Gson mGson;

  /**
   * The time of the lost written update of log files, intitially MIN_VALUE + 1 because it has to be
   * after the last cache refresh time
   */
  private long lastLogUpdateTime = 1;

  /** Folder for recipes */
  private String recipePath = PropertyUtil.RECIPE_PATH;

  /** Folder for logs */
  private String logPath = PropertyUtil.LOG_PATH;

  /**
   * This method creates an instance of the PersistenceHandler class, creates a log entry and sets
   * the Gson instance.
   */
  public PersistenceHandler() {
    LOGGER.info("PersistenceHandler created");
    mGson = Serializer.getInstance();
  }

  /**
   * Creates a file and adds a number to the end of its name if a file with this name already
   * exists.
   *
   * @param folder the folder in which the new file will be created
   * @param name name of the new file (not including extension)
   * @param ext file extension of the file
   * @return a file object representing the newly created file
   * @throws FileCreationException the file creation exception
   */
  private File createFile(final String folder, final String name, final String ext)
      throws FileCreationException {
    File file = new File(folder + File.separator + name + ext);
    try {
      for (int i = 1; file.exists(); i++) {
        file = new File(folder + File.separator + name + "(" + i + ")" + ext);
      }
      file.getParentFile().mkdirs();
      file.createNewFile();
    } catch (IOException e) {
      LOGGER.error(e);
      throw new FileCreationException(e);
    }
    return file;
  }

  /**
   * Creates a recipe object from a file.
   *
   * @param file recipe file
   * @return recipe object created from file contents
   * @throws RecipeNotFoundException if the file does not exist
   * @throws RecipeParseException if the file could not be parsed
   */
  private Recipe getRecipe(final File file) throws RecipeNotFoundException, RecipeParseException {
    if (!file.exists()) {
      throw new RecipeNotFoundException();
    }
    try {
      Recipe recipe = reader.getRecipeByString(new String(Files.readAllBytes(file.toPath())));
      String fileName = file.getName();
      if (fileName != null) {
        fileName = fileName.replace(PropertyUtil.RECIPE_FILE_EXT, "");
      }
      recipe.setId(fileName);
      return recipe;
    } catch (IOException e) {
      LOGGER.error(e);
      throw new RecipeParseException(e);
    }
  }

  @Override
  public Recipe getRecipe(final String id) throws RecipeNotFoundException, RecipeParseException {
    File recipeFile = new File(recipePath + File.separator + id + PropertyUtil.RECIPE_FILE_EXT);
    return getRecipe(recipeFile);
  }

  @Override
  public List<RecipeSummary> getRecipeSummaries() throws RecipeParseException {
    File folder = new File(recipePath);
    File[] recipeFiles = folder.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File file, final String name) {
        return name.endsWith(PropertyUtil.RECIPE_FILE_EXT);
      }
    });
    if (recipeFiles == null || recipeFiles.length == 0) {
      return new ArrayList<RecipeSummary>();
    }
    RecipeReader reader = new RecipeReader();
    List<RecipeSummary> summaries = new ArrayList<RecipeSummary>();
    for (File file : recipeFiles) {
      try {
        RecipeSummary summary =
            reader.getRecipeSummaryByString(new String(Files.readAllBytes(file.toPath())));
        String fileName = file.getName();
        if (fileName != null) {
          fileName = fileName.replace(PropertyUtil.RECIPE_FILE_EXT, "");
        }
        summary.setId(fileName);
        summaries.add(summary);
      } catch (IOException e) {
        // Do nothing, broken recipe files will be ignored for summary
        // index
      }
    }
    return summaries;
  }

  @Override
  public File saveLog(final BrewingLog log) throws LogSavingException {
    String json = mGson.toJson(log);
    FileOutputStream out;


    try {
      File file =
          createFile(logPath, log.getId() + "_" + dateFormat.format((new Date()).getTime()),
              PropertyUtil.LOG_FILE_EXT);
      out = new FileOutputStream(file, false);
      out.write(json.getBytes());
      out.flush();
      out.close();
      lastLogUpdateTime = (new Date()).getTime();
      return file;
    } catch (IOException | FileCreationException e) {
      LOGGER.error(e);
      throw new LogSavingException(e);
    }

  }

  @Override
  public String saveRecipe(final Recipe recipe) throws RecipeParseException, RecipeSavingException {
    RecipeWriter recipeWriter = new RecipeWriter();
    String id = recipe.getId();
    if (id == null || id.isEmpty()) {
      id = dateFormat.format((new Date()).getTime());
      recipe.setId(id);
    }
    FileOutputStream out;
    try {
      File recipeFile = new File(recipePath + File.separator + id + PropertyUtil.RECIPE_FILE_EXT);
      String count = "";
      for (int i = 0; recipeFile.exists(); i++) {
        count = "(" + i + ")";
        recipeFile =
            new File(recipePath + File.separator + id + count + PropertyUtil.RECIPE_FILE_EXT);
      }
      id += count;
      recipe.setId(id);
      recipeFile.getParentFile().mkdirs();
      recipeFile.createNewFile();
      String xml = recipeWriter.convertRecipeToXml(recipe);
      out = new FileOutputStream(recipeFile, false);
      out.write(xml.getBytes());
      out.close();
    } catch (IOException e) {
      LOGGER.error(e);
      throw new RecipeSavingException(e);
    }
    return recipe.getId();
  }

  /** sets the directory for recipes */
  public void setRecipePath(final String path) {
    recipePath = path;
  }

  /** sets the directory for logs */
  public void setLogPath(final String path) {
    logPath = path;
  }

  /** gets the directory for recipes */
  public String getRecipePath() {
    return recipePath;
  }

  /** gets the directory for logs */
  public String getLogPath() {
    return logPath;
  }

  /** Gets the time of the last written update of the log files */
  @Override
  public long getLastLogUpdateTime() {
    return lastLogUpdateTime;
  }

  @Override
  public BrewingLog getLogById(final int id) throws LogNotFoundException, LogParseException {
    File[] files = (new File(logPath)).listFiles(new FilenameFilter() {

      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(PropertyUtil.LOG_FILE_EXT);
      }
    });
    if (files != null) {
      for (File file : files) {
        if (file.getName().startsWith(id + "_")) {
          try {
            return getLogByString(new String(Files.readAllBytes(file.toPath())));
          } catch (IOException e) {
            LOGGER.error(e);
            throw new LogParseException(e);
          }
        }
      }
    }
    throw new LogNotFoundException();
  }

  /**
   * creates a log object from its string representation.
   * 
   * @param log string representation of the log
   * @return the created log object
   * @throws LogParseException
   */
  public BrewingLog getLogByString(final String log) throws LogParseException {
    try {
      return mGson.fromJson(log, BrewingLog.class);
    } catch (JsonSyntaxException e) {
      LOGGER.error(e);
      throw new LogParseException(e);
    }
  }

  @Override
  public List<BrewingLog> getLogs() {
    List<BrewingLog> result = new ArrayList<BrewingLog>();
    File[] files = (new File(logPath)).listFiles(new FilenameFilter() {

      @Override
      public boolean accept(final File dir, final String name) {
        return name.endsWith(PropertyUtil.LOG_FILE_EXT);
      }
    });
    if (files == null) {
      return result;
    }
    for (File file : files) {
      try {
        result.add(getLogByString(new String(Files.readAllBytes(file.toPath()))));
      } catch (IOException | LogParseException e) {
        // do nothing, broken files will be ignored
      }
    }
    return result;
  }

  @Override
  public List<Integer> getUsedIds() {
    List<BrewingLog> logs = getLogs();
    List<Integer> ids = new ArrayList<Integer>();
    for (BrewingLog log : logs) {
      ids.add(log.getId());
    }
    return ids;
  }

  /** returns the lowest unused id, i.e. Max(getUsedIds())+1 */
  public int getLowestUnusedId() {
    int max = 0;
    for (Integer i : getUsedIds()) {
      if (i > max) {
        max = i;
      }
    }
    return max + 1;
  }
}
