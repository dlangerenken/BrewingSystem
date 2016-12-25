/*
 *
 */
package messages;

import general.BrewingState;
import general.MessagePriority;


/**
 * This Message-Type gives the clients the possibility to see, which confirmation (if any) is
 * expected, so that they can confirm these steps.
 */
public class PreNotificationMessage extends Message {

  /**
   * Sets the content-message of the prenotification, required for serialization
   * 
   * @param mContent Message which will be notified shortly
   */
  public void setContent(final Message mContent) {
    this.mContent = mContent;
  }

  /** message which will be send after mTimeToAdd */
  private Message mContent;

  /** BrewingState which this confirmation is related to. */
  private final BrewingState mBrewingState;

  /**
   * This is for sending a pre-notification with another than the default time
   */
  private long millisToNotification;

  /**
   * Initializes a pre notification message with extra data (if required), which tells us, what kind
   * of message is going to be received after timeToAdd.
   *
   * @param brewingState the brewing state
   * @param message the message
   */
  public PreNotificationMessage(final BrewingState brewingState, final Message message,
      final long millisToNotification) {
    mContent = message;
    mBrewingState = brewingState;
    setMillisToNotification(millisToNotification);
    setPriority(MessagePriority.HIGH);

  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public Message getContent() {
    return mContent;
  }

  /**
   * Gets the brewing state.
   *
   * @return the brewing state
   */
  public BrewingState getBrewingState() {
    return mBrewingState;
  }

  /**
   * Returns the milliseconds to notifications
   *
   * @return millisToNotification the time this notification precedes the real notification
   */
  public long getMillisToNotification() {
    return millisToNotification;
  }

  /**
   * Sets a different than the default pre-notification time
   *
   * @param millisToNotification
   */
  public void setMillisToNotification(final long millisToNotification) {
    this.millisToNotification = millisToNotification;
  }
}
