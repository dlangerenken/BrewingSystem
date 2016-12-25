package messages;

import general.BrewingState;
import general.MessagePriority;

/**
 * 
 * @author Patrick
 *
 *         A message class that is used for all messages created after a confirmation by the user,
 *         independent if it was a valid one.
 */
public class ConfirmationMessage extends Message {

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((confirmedState == null) ? 0 : confirmedState.hashCode());
    result = prime * result + ((currentState == null) ? 0 : currentState.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConfirmationMessage other = (ConfirmationMessage) obj;
    if (confirmedState == null) {
      if (other.confirmedState != null) {
        return false;
      }
    } else if (!confirmedState.equals(other.confirmedState)) {
      return false;
    }
    if (currentState == null) {
      if (other.currentState != null) {
        return false;
      }
    } else if (!currentState.equals(other.currentState)) {
      return false;
    }
    return true;
  }

  private final BrewingState currentState;
  private final BrewingState confirmedState;

  /**
   * Creates a message for every brewing state confirmation sent by the client to the brewing
   * controller
   *
   * @param currentState the current brewing state in the controller
   * @param confirmedState the brewing state that was (tried to) confirmed
   */
  public ConfirmationMessage(final BrewingState currentState, final BrewingState confirmedState) {
    setPriority(MessagePriority.HIGH);
    this.currentState = currentState;
    this.confirmedState = confirmedState;
  }

  /**
   * Returns the current brewing state
   *
   * @return the current brewing state in the controller
   */
  public BrewingState getCurrentState() {
    return currentState;
  }

  /**
   * Returns the confirmed brewing state
   *
   * @return the brewing state that was (tried to) confirmed
   */
  public BrewingState getConfirmedState() {
    return confirmedState;
  }
}
