/*
 *
 */
package general;


/**
 * Different priority level for a message Always = Should always be within a log/protocol Very_low =
 * Not necessarily important for clients to see.
 *
 * @author Daniel Langerenken
 */
public enum MessagePriority {

  /** For messages that are always included. */
  ALWAYS(0),
  /** For messages with very high priority. */
  VERY_HIGH(1),
  /** For messages with high priority */
  HIGH(2),
  /** For medium priority messages */
  MEDIUM(3),
  /** For messages with low priority */
  LOW(4),
  /** For messages of very little significance */
  VERY_LOW(5);

  /**
   * Creates priority from integer. 0 = always, 5 = very_low
   *
   * @param priority the priority as int
   * @return the message priority
   */
  public static MessagePriority fromInt(final int priority) {
    switch (priority) {
      case 0:
        return ALWAYS;
      case 1:
        return VERY_HIGH;
      case 2:
        return HIGH;
      case 3:
        return MEDIUM;
      case 4:
        return LOW;
      case 5:
        return VERY_LOW;
    }
    return ALWAYS;
  }

  /** The value. */
  private final int value;

  /**
   * Instantiates a new message priority.
   *
   * @param newValue the priority value, 0 = always, 5 = very_low
   */
  private MessagePriority(final int newValue) {
    value = newValue;
  }

  /**
   * Gets an integer representing the priority, 0 = always, 5 = very_low
   *
   * @return the value
   */
  public int getValue() {
    return value;
  }
}
