package general;

import java.util.ArrayList;
import java.util.List;


/**
 * For Issue #161 - Class offers basic information about the current brewing procss (not only the
 * state but also basic information)
 * 
 * @author Daniel Langerenken
 *
 */
public class BrewingProcessSummary {

  /**
   * Provides information about the current TemperatureLevel to be shown to the client.
   * 
   * @author patrick
   *
   */
  public static class TemperatureLevelInfo {
    /* the position of the temperature level in the original recipe */
    public final int position;
    /* start of the level as specified in the recipe */
    public final long recipeStartTime;
    /* duration of the level */
    public final long duration;
    /* temperature */
    public final float temperature;
    /* boolean to show if the level was already started */
    public final boolean alreadyStarted;
    /* the system time in milliseconds when the level was started */
    public final long startTimeMillis;

    /**
     * Instantiates a temperature-level info by passing positin, duration, temperature etc
     * 
     * @param position
     * @param recipeStartTime
     * @param duration
     * @param temperature
     * @param alreadyStarted
     * @param startTimeMillis
     */
    public TemperatureLevelInfo(final int position, final long recipeStartTime, final long duration,
        final float temperature, final boolean alreadyStarted, final long startTimeMillis) {
      this.position = position;
      this.recipeStartTime = recipeStartTime;
      this.duration = duration;
      this.temperature = temperature;
      this.alreadyStarted = alreadyStarted;
      this.startTimeMillis = startTimeMillis;
    }
  }

  /** Current BrewingState of the brewing process */
  private BrewingState brewingState;

  /** Start time of mashing in the brewing process */
  private long startTimeMashing;

  /** Start time of hop-cooking in the brewing process */
  private long startTimeHopCooking;

  /** The id of the recipe used for the brewing process */
  private String recipeId;

  /** The temperature level info for the client */
  private final ArrayList<TemperatureLevelInfo> temperatureLevelInfo;

  /**
   * Instantiates the brewing-state-summary
   * 
   * @param brewingState brewing state of the brewing process
   * @param startTimeMashing start time of the mashing procedure
   * @param startTimeHopCooking start time of the hop cooking procedure
   */
  public BrewingProcessSummary(final BrewingState brewingState) {
    this.brewingState = brewingState;
    this.startTimeHopCooking = -1;
    this.startTimeMashing = -1;
    this.temperatureLevelInfo = new ArrayList<BrewingProcessSummary.TemperatureLevelInfo>();
    recipeId = null;
  }

  /**
   * Returns the current brewing state
   * 
   * @return brewing state of the process
   */
  public BrewingState getBrewingState() {
    return brewingState;
  }

  /**
   * start time of the mashing process
   * 
   * @return point in time where the mashing should start
   */
  public long getStartTimeMashing() {
    return startTimeMashing;
  }

  /**
   * start time of the hop cooking process
   * 
   * @return point in time where the hop cooking should start
   */
  public long getStartTimeHopCooking() {
    return startTimeHopCooking;
  }

  /**
   * Id of the recipe for the brewing process.
   * 
   * @return
   */
  public String getRecipeId() {
    return recipeId;
  }

  /**
   * Sets the new brewing State.
   * 
   * @param brewingState
   */
  public void setBrewingState(final BrewingState brewingState) {
    this.brewingState = brewingState;
  }

  /**
   * Sets the startTimeMashing, if none was already set.
   * 
   * @param startTimeMashing
   */
  public void setStartTimeMashing(final long startTimeMashing) {
    if (this.startTimeMashing < 0) {
      this.startTimeMashing = startTimeMashing;
    } else {
      throw new RuntimeException("StartTimeMashing was already set.");
    }
  }

  /**
   * Sets the startTimeHopCooking, if none was already set.
   * 
   * @param startTimeHopCooking
   */
  public void setStartTimeHopCooking(final long startTimeHopCooking) {
    if (this.startTimeHopCooking < 0) {
      this.startTimeHopCooking = startTimeHopCooking;
    } else {
      throw new RuntimeException("StartTimeHopCooking was already set.");
    }
  }

  /**
   * Sets the recipeId, if none was already set.
   * 
   * @param recipeId
   */
  public void setRecipeId(final String recipeId) {
    if (this.recipeId == null) {
      this.recipeId = recipeId;
    } else {
      throw new RuntimeException("RecipeId was already set.");
    }
  }

  /**
   * Returns the list of temperature level infos
   * 
   * @return
   */
  public List<TemperatureLevelInfo> getTemperatureLevelInfo() {
    return temperatureLevelInfo;
  }

  /**
   * Clears the list of temperature level information
   */
  public void clearTemperatureLevelInfo() {
    temperatureLevelInfo.clear();
  }
}
