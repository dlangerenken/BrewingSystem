package messages;

import general.MessagePriority;
import general.BrewingState.State;

/**
 * Messages which shows if a brewing-state was ended
 *
 */
public class EndMessage extends Message {

  /**
   * BrewingState which is ended
   */
  private final State brewingState;

  /**
   * Instantiates the message with a brewing state
   * 
   * @param brewingState state which is ended
   */
  public EndMessage(final State brewingState) {
    setPriority(MessagePriority.ALWAYS);
    this.brewingState = brewingState;
  }

  /**
   * Returns the current brewing-state position
   * 
   * @return brewing-state which was ended
   */
  public State getPosition() {
    return brewingState;
  }
}
