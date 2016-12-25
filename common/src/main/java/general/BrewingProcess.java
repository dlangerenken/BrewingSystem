/*
 *
 */
package general;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Class which stores the whole interaction of a brewing process.
 */
public class BrewingProcess implements Serializable {

  /** not necessary serial version uid. */
  private static final long serialVersionUID = 1L;

  /** Recipe which should be used for the brewing. */
  private Recipe recipe;

  /** List of iodine-tests which were made during the process. */
  private final List<IodineTest> iodineTests;

  /** Log which is created during the process. */
  private final BrewingLog brewingLog;

  /** Current brewing-state. */
  private final BrewingState state;

  /** start time of the brewing process. */
  private long startTime;

  /** end time of the brewing process if already finished. */
  private long endTime;

  /**
   * LogIdCounter which is used for receiving unique log-ids
   */
  private final int logIdCounter;

  /**
   * Instantiates a new brewing process.
   */
  public BrewingProcess(final Recipe recipe, int logIdCounter) {
    this.logIdCounter = logIdCounter;
    brewingLog = new BrewingLog(recipe, logIdCounter++);
    iodineTests = new CopyOnWriteArrayList<IodineTest>();
    state =
        new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.NOT_STARTED,
            BrewingState.Position.START);
    this.recipe = recipe;
  }

  /**
   * Returns the current log id counter
   * 
   * @return log id counter
   */
  public int getLogId() {
    return logIdCounter;
  }

  /**
   * Log of the brewing process.
   *
   * @return log which was created during the process
   */
  public BrewingLog getBrewingLog() {
    return brewingLog;
  }

  /**
   * EndTime of the process.
   *
   * @return endTime in milliseconds of the process
   */
  public long getEndTime() {
    return endTime;
  }

  /**
   * All Iodine-Tests.
   *
   * @return iodine-tests which were made during the process
   */
  public List<IodineTest> getIodineTests() {
    return iodineTests;
  }

  /**
   * Returns the recipe.
   *
   * @return recipe of the process
   */
  public Recipe getRecipe() {
    return recipe;
  }

  /**
   * StartTime of the process.
   *
   * @return starttime in milliseconds of the process
   */
  public long getStartTime() {
    return startTime;
  }

  /**
   * Current brewing state.
   *
   * @return enum (Mashing, Lautering, ...) of the current process
   */
  public BrewingState getState() {
    return state;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((brewingLog == null) ? 0 : brewingLog.hashCode());
    result = prime * result + (int) (endTime ^ (endTime >>> 32));
    result = prime * result + ((iodineTests == null) ? 0 : iodineTests.hashCode());
    result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
    result = prime * result + (int) (startTime ^ (startTime >>> 32));
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BrewingProcess other = (BrewingProcess) obj;
    if (brewingLog == null) {
      if (other.brewingLog != null) {
        return false;
      }
    } else if (!brewingLog.equals(other.brewingLog)) {
      return false;
    }
    if (endTime != other.endTime) {
      return false;
    }
    if (iodineTests == null) {
      if (other.iodineTests != null) {
        return false;
      }
    } else if (!iodineTests.equals(other.iodineTests)) {
      return false;
    }
    if (recipe == null) {
      if (other.recipe != null) {
        return false;
      }
    } else if (!recipe.equals(other.recipe)) {
      return false;
    }
    if (startTime != other.startTime) {
      return false;
    }
    if (state == null) {
      if (other.state != null) {
        return false;
      }
    } else if (!state.equals(other.state)) {
      return false;
    }
    return true;
  }

  /**
   * Sets the recipe.
   *
   * @param recipe recipe which was used for the brewing process
   */
  public void setRecipe(final Recipe recipe) {
    this.recipe = recipe;
  }

  /**
   * Starts the brewing process by setting start time to the current system time.
   */
  public void start() {
    startTime = System.currentTimeMillis();
  }

  /**
   * Finishes the brewing process by setting end time to the current system time.
   */
  public void finish() {
    endTime = System.currentTimeMillis();
  }

  /**
   * Changes the state of the brewing process. (Rather use setState(final BrewingState.Type type,
   * final BrewingState.State state, final BrewingState.Position position) to avoid creating
   * unnecessary new objects
   *
   * @param state new state of the brewing process, may not be null
   */
  public void setState(final BrewingState state) {
    setState(state.getType(), state.getState(), state.getPosition());
  }

  /**
   * Changes the state of the brewing process.
   *
   * @param type new type
   * @param state new state
   * @param position new position
   */
  public void setState(final BrewingState.Type type, final BrewingState.State state,
      final BrewingState.Position position) {
    changeState(type);
    changeState(state);
    changeState(position);
  }

  /**
   * Changes the brewing state type.
   *
   * @param type the type
   */
  public void changeState(final BrewingState.Type type) {
    state.setType(type);
  }

  /**
   * changes the brewing state state.
   *
   * @param state the state
   */
  public void changeState(final BrewingState.State state) {
    this.state.setState(state);
  }

  /**
   * changes the brewing state position.
   *
   * @param position the position
   */
  public void changeState(final BrewingState.Position position) {
    state.setPosition(position);
  }
}
