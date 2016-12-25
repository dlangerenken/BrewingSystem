/*
 * 
 */
package push;


/**
 * A Message which can be sent to the client.
 *
 * @author Daniel Langerenken
 */
public class PushMessage {
  /**
   * The push-type of the message (e.g. INFO, ALARM, MANUAL_STEP)
   */
  private final PushType type;

  /** The corresponding data for the push type, either string or gson-string. */
  private final String data;

  /**
   * Creates a push-message with a push-type and content-string.
   *
   * @param type push-type of the message (e.g. INFO, ALARM, MANUAL_STEP)
   * @param data corresponding data for the push type, either string or gson-string
   */
  public PushMessage(final PushType type, final String data) {
    this.type = type;
    this.data = data;
  }

  /**
   * Data of message.
   *
   * @return Data (e.g. gson(message), String)
   */
  public String getData() {
    return data;
  }

  /**
   * PushType of Message.
   *
   * @return PushType (e.g. INFO, ALARM)
   */
  public PushType getPushType() {
    return type;
  }

}
