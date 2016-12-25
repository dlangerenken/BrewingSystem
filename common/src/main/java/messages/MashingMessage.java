/*
 *
 */
package messages;

import general.MessagePriority;

/**
 * A message send during the mashing process
 */
public class MashingMessage extends Message {
  /** Creates a new MashingMessage with medium priority */
  public MashingMessage() {
    setPriority(MessagePriority.MEDIUM);

  }

}
