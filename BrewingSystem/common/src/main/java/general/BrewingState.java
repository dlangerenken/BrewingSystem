/*
 * 
 */
package general;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * Enumeration for every single step of the brewing process XYZ - X = BrewingState, Y = Requested if
 * 0, otherwise.
 */
public class BrewingState {

  /**
   * Gives information about the current brewing state (e.g. WHICH ingredient needs to be added)
   */
  private Object data;

  /**
   * Checks if the brewing state was cancelled
   * 
   * @return true, if cancelled
   */
  public boolean isCancelled() {
    return getType() == Type.CANCEL;
  }

  /**
   * Position of the brewing process (e.g. beginning of mashing, end of lautering, iodine-test)
   *
   */
  public enum Position {

    /** The start position (Might not be started yet (if type == requested). */
    START(10),
    /** "Middle"-Position (currently ongoing in the current step). */
    ONGOING(20),
    /** End-Position. */
    END(30),
    /** Iodine-Test Position */
    IODINE(40),
    /** Ingredient-Addition Position **/
    ADDING(50);

    /**
     * Returns the brewing position from the given number.
     *
     * @param stateValue number of the brewing position
     * @return enum-object of the brewing position number
     */
    public static Position fromInt(final int stateValue) {
      for (Position state : Position.values()) {
        if (state.getValue() == stateValue) {
          return state;
        }
      }
      return ONGOING;
    }

    /** Current value as number. */
    private final int value;

    /**
     * position as number.
     *
     * @param newValue number for the brewing state
     */
    private Position(final int newValue) {
      value = newValue;
    }

    /**
     * Returns brewing position equivalent number.
     *
     * @return number which is equivalent to the enum
     */
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      switch (this) {
        case START:
          return "START(" + value + ")";
        case ONGOING:
          return "ONGOING(" + value + ")";
        case IODINE:
          return "IODINE(" + value + ")";
        case ADDING:
          return "ADDING(" + value + ")";
        case END:
          return "END(" + value + ")";
        default:
          throw new NotImplementedException();
      }
    }
  }

  /**
   * Current brewing state such as MASHING, LAUTERING, HOP_COOKING.
   */
  public enum State {

    /** The not started state (brewing process requested, but nothing done yet). */
    NOT_STARTED(100),
    /** The mashing state (brewing process is in mashing). */
    MASHING(200),
    /** The lautering state (brewing process is in lautering). */
    LAUTERING(300),
    /** The hop cooking state (brewing process is in hop-cooking). */
    HOP_COOKING(400),
    /** The whirlpool state (brewing process is in whirlpool). */
    WHIRLPOOL(500),
    /** The finished state (brewing process is finished, yet not completely "closed"). */
    FINISHED(600);

    /**
     * Returns the brewing state from the given number.
     *
     * @param stateValue number of the brewing state
     * @return enum-object of the brewing state number
     */
    public static State fromInt(final int stateValue) {
      for (State state : State.values()) {
        if (state.getValue() == stateValue) {
          return state;
        }
      }
      return NOT_STARTED;
    }

    /** Current value as number. */
    private final int value;

    /**
     * BrewingState as number.
     *
     * @param newValue number for the brewing state
     */
    private State(final int newValue) {
      value = newValue;
    }

    /**
     * Returns brewing process equivalent number.
     *
     * @return number which is equivalent to the enum
     */
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      switch (this) {
        case NOT_STARTED:
          return "NOT_STARTED(" + value + ")";
        case MASHING:
          return "MASHING(" + value + ")";
        case LAUTERING:
          return "LAUTERING(" + value + ")";
        case HOP_COOKING:
          return "HOP_COOKING(" + value + ")";
        case WHIRLPOOL:
          return "WHIRLPOOL(" + value + ")";
        case FINISHED:
          return "FINISHED(" + value + ")";
        default:
          throw new NotImplementedException();
      }
    }
  }

  /**
   * Type of the current brewing state (whether it runs normally or requests something from the
   * user.
   */
  public enum Type {

    /** The normal type (brewing process is ongoing, nothing to do here). */
    NORMAL(1),
    /** The request type (brewing process is interrupted, user interaction required). */
    REQUEST(2),
    /** The cancel type (brewing process is aborted, needs to be restarted). */
    CANCEL(3),
    /** An intern type for communication between Masher/HopCooker and BrewingController. */
    INTERN(4);

    /**
     * Returns the brewing type from the given number.
     *
     * @param stateValue number of the brewing type
     * @return enum-object of the brewing type number
     */
    public static Type fromInt(final int stateValue) {
      for (Type state : Type.values()) {
        if (state.getValue() == stateValue) {
          return state;
        }
      }
      return NORMAL;
    }

    /** Current type as number. */
    private final int value;

    /**
     * Type as number.
     *
     * @param newValue number for the brewing state
     */
    private Type(final int newValue) {
      value = newValue;
    }

    /**
     * Returns type equivalent number.
     *
     * @return number which is equivalent to the type
     */
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      switch (this) {
        case NORMAL:
          return "NORMAL(" + value + ")";
        case REQUEST:
          return "REQUEST(" + value + ")";
        case CANCEL:
          return "CANCEL(" + value + ")";
        case INTERN:
          return "INTERN(" + value + ")";
        default:
          throw new NotImplementedException();
      }
    }
  }

  /**
   * Converts a XYZ-Code to a brewing state.
   *
   * @param value given XYZ code (if invalid it will use default values
   * @return brewing-state which is equivalent to the XYZ-value
   */
  public static BrewingState fromValue(final int value) {
    State state = State.fromInt(value - (value % 100));
    Position position = Position.fromInt((value % 100) - (value % 10));
    Type type = Type.fromInt(value % 10);
    return new BrewingState(type, state, position);
  }

  /**
   * Converts the given brewing-state to a number.
   *
   * @param state -state which should be converted to XYZ
   * @return (XYZ) where X = state, Y = position and Z = type
   */
  public static int toValue(final BrewingState state) {
    return state.getType().getValue() + state.getState().getValue()
        + state.getPosition().getValue();
  }

  /**
   * The current brewing-type (e.g. REQUEST)
   */
  private Type type;

  /**
   * Current brewing state (e.g. MASHING)
   */
  private State state;

  /**
   * Current position (e.g. START, END)
   */
  private Position position;

  /**
   * Initializes the brewing state with a 3-tupel.
   *
   * @param type type of the brewing process
   * @param state state of the current brewing process
   * @param position current position of the brewing process within the state
   */
  public BrewingState(final Type type, final State state, final Position position) {
    this(type, state, position, null);
  }

  /**
   * Initializes the brewing state with a 3-tupel.
   *
   * @param type type of the brewing process
   * @param state state of the current brewing process
   * @param position current position of the brewing process within the state
   * @param data current data which gives useful information about the state (e.g. which ingredient
   *        needs to be added)
   */
  public BrewingState(final Type type, final State state, final Position position, final Object data) {
    this.type = type;
    this.state = state;
    this.position = position;
    this.data = data;
  }

  /**
   * retuns the current data of the brewing state. This can be a IodineTest object, or a
   * List<IngredientAddition>
   *
   * @return current data
   */
  public Object getData() {
    return data;
  }

  /**
   * retuns the current position of the brewing state.
   *
   * @return current position
   */
  public Position getPosition() {
    return position;
  }

  /**
   * retuns the current state of the brewing state.
   *
   * @return current state
   */
  public State getState() {
    return state;
  }

  /**
   * retuns the current type of the brewing state.
   *
   * @return current type
   */
  public Type getType() {
    return type;
  }

  /**
   * Method which will tell you if the current state needs a response from the client.
   *
   * @return true, if in request-state, false otherwise
   */
  public boolean requestNeeded() {
    return type == Type.REQUEST;
  }

  /**
   * sets new data for the brewing state.
   *
   * @param data new data
   */
  public void setData(final Object data) {
    this.data = data;
  }

  /**
   * sets a new position for the brewing state.
   *
   * @param position new position
   */
  public void setPosition(final Position position) {
    this.position = position;
  }

  /**
   * sets a new state for the brewing state.
   *
   * @param state new state
   */
  public void setState(final State state) {
    this.state = state;
  }

  /**
   * sets a new type for the brewing state.
   *
   * @param type new type
   */
  public void setType(final Type type) {
    this.type = type;
  }

  /**
   * Converts the current brewing-state to a number.
   *
   * @return (XYZ) where X = state, Y = position and Z = type
   */
  public int toValue() {
    return toValue(this);
  }



  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    BrewingState other = (BrewingState) obj;
    if (data == null) {
      if (other.data != null) {
        return false;
      }
    } else if (!data.equals(other.data)) {
      return false;
    }
    if (position != other.position) {
      return false;
    }
    if (state != other.state) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "(" + state + "," + position + "," + type + ")";
  }

  /**
   * Checks if another BrewingState with Type t, State s and Position p is equal to this one,
   * meaning it has the same type, state and position
   * 
   * @param t
   * @param s
   * @param p
   * @return
   */
  public boolean equals(final Type t, final State s, final Position p) {
    return getType() == t && getState() == s && getPosition() == p;
  }
}
