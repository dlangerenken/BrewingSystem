/*
 *
 */
package messages;

import general.MaltAddition;
import general.MessagePriority;

import java.util.Collection;


/**
 * Signals that an ingredient needs to be added in mashing.
 */
public class MaltAdditionMessage extends MashingMessage {

  /** The malt addition. */
  private final Collection<MaltAddition> maltAdditions;

  /**
   * Creates a new IngredientAdditionMessage containing a maltAddition to be performed next.
   *
   * @param maltAdditions
   */
  public MaltAdditionMessage(final Collection<MaltAddition> maltAdditions) {
    setPriority(MessagePriority.ALWAYS);
    this.maltAdditions = maltAdditions;
  }

  /**
   * Gets the malt addition.
   *
   * @return the malt addition
   */
  public Collection<MaltAddition> getMaltAdditions() {
    return maltAdditions;
  }
}
