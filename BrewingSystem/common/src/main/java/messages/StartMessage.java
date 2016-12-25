package messages;

import general.MessagePriority;
import general.BrewingState.State;

/**
 * Messages which shows if a brewing-state was started
 *
 */
public class StartMessage extends Message {

  /**
   * BrewingState which is started
   */
  private final State brewingState;

  /**
   * Instantiates the message with a brewing state
   * @param brewingState state which is started
   */
  public StartMessage(final State brewingState) {
    setPriority(MessagePriority.ALWAYS);
    this.brewingState = brewingState;
  }

  /**
   * Returns the current brewing-state position
   * @return brewing-state which was started
   */
  public State getPosition() {
    return brewingState;
  }
}
