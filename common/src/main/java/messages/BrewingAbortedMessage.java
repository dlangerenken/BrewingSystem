package messages;

import general.MessagePriority;

/**
 * A message to be sent in case of an aborted brewing process.
 *
 * @author Patrick
 *
 */
public class BrewingAbortedMessage extends Message {
  /** The reason why brewing was aborted */
  private final String reason;

  /**
   * Creates a new BrewingAbortedMessage
   *
   * @param reason the reason for abortion
   */
  public BrewingAbortedMessage(final String reason) {
    setPriority(MessagePriority.ALWAYS);
    this.reason = reason;
  }

  /**
   * Retrieves the reason for abortion
   *
   * @return
   */
  public String getReason() {
    return reason;
  }
}
