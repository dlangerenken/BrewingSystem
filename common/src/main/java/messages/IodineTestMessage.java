/*
 *
 */
package messages;

import general.IodineTest;
import general.MessagePriority;


/**
 * Is send when an iodine test has to be performed.
 */
public class IodineTestMessage extends MashingMessage {

  /** The iodine test object. */
  private final IodineTest iodineTest;

  /**
   * Gets the iodine test.
   *
   * @return the iodine test
   */
  public IodineTest getIodineTest() {
    return iodineTest;
  }

  /**
   * Creates a iodine test message.
   *
   * @param iodineTest the new iodine test
   */
  public IodineTestMessage(final IodineTest iodineTest) {
    setPriority(MessagePriority.ALWAYS);
    this.iodineTest = iodineTest;
  }
}
