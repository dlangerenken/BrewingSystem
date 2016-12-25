/*
 *
 */
package messages;

import general.HopAddition;
import general.MessagePriority;

import java.util.List;


/**
 * Signals that a hop addition is required.
 */
public class HopAdditionMessage extends Message {

  /** The hop addition. */
  private List<HopAddition> hopAdditions;

  /** creates a new HopCookingMessage with always-priority */
  public HopAdditionMessage(final List<HopAddition> add) {
    hopAdditions = add;
    setPriority(MessagePriority.ALWAYS);
  }

  /**
   * Gets the hop addition.
   *
   * @return the hop addition
   */
  public List<HopAddition> getHopAddition() {
    return hopAdditions;
  }

  /**
   * Sets the hop addition.
   *
   * @param hopAddition the new hop addition
   */
  public void setHopAddition(final List<HopAddition> hopAdditions) {
    this.hopAdditions = hopAdditions;
  }
}
