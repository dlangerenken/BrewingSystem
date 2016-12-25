/*
 *
 */
package messages;

import general.MessagePriority;


/**
 * parent class for all messages
 */
public class Message implements Comparable<Message> {

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((priority == null) ? 0 : priority.hashCode());
    result = prime * result + (int) (time ^ (time >>> 32));
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
    Message other = (Message) obj;
    if (message == null) {
      if (other.message != null) {
        return false;
      }
    } else if (!message.equals(other.message)) {
      return false;
    }
    if (priority != other.priority) {
      return false;
    }
    if (time != other.time) {
      return false;
    }
    return true;
  }

  /** The time this message was sent. */
  private long time;

  /** The message content. */
  private String message;
  /**
   * This object declares the priority of the message (e.g. whether the client should receive this
   * message if asked for "update"
   */
  private MessagePriority priority = MessagePriority.VERY_LOW;

  /**
   * creates a message with lowest priority and current system time and empty message string.
   */
  public Message() {
    this(null);
  }

  /**
   * Default constructor that creates a Message with a given string, the current system time and the
   * lowest priority.
   *
   * @param message the message
   */
  public Message(final String message) {
    setMessage(message);
    setTime(System.currentTimeMillis());
    setPriority(MessagePriority.VERY_LOW);
  }

  /**
   * sets the time excplicitly. should only be used by PersistanceHandler to reconstruct messages
   * from files.
   *
   * @param message the message
   * @param time the time
   */
  public Message(final String message, final long time) {
    setMessage(message);
    setTime(time);
    setPriority(MessagePriority.VERY_LOW);
  }


  @Override
  public int compareTo(final Message o) {
    if (o == null) {
      return 1;
    }
    return Long.compare(getTime(), o.getTime());
  }

  /**
   * Gets the message content.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the priority of the message.
   *
   * @return the priority
   */
  public MessagePriority getPriority() {
    return priority;
  }

  /**
   * Gets the time.
   *
   * @return the time
   */
  public long getTime() {
    return time;
  }

  /**
   * Sets the message.
   *
   * @param message the new message
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /**
   * Sets the priority.
   *
   * @param priority the new priority
   */
  public void setPriority(final MessagePriority priority) {
    this.priority = priority;
  }

  /**
   * Sets the time.
   *
   * @param time the new time
   */
  public void setTime(final long time) {
    this.time = time;
  }
}
