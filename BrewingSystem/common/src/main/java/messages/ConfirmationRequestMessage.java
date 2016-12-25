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
public class ConfirmationRequestMessage extends Message {

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((mBrewingStep == null) ? 0 : mBrewingStep.hashCode());
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
    ConfirmationRequestMessage other = (ConfirmationRequestMessage) obj;
    if (mBrewingStep == null) {
      if (other.mBrewingStep != null) {
        return false;
      }
    } else if (!mBrewingStep.equals(other.mBrewingStep)) {
      return false;
    }
    return true;
  }

  /** BrewingState which is this confirmation related to. */
  private BrewingState mBrewingStep;

  /**
   * Initializes a confirmation request message.
   *
   * @param brewingStep the brewing step
   */
  public ConfirmationRequestMessage(final BrewingState brewingStep) {
    setPriority(MessagePriority.ALWAYS);
    if (brewingStep == null) {
      mBrewingStep = null;
    } else {
      mBrewingStep = BrewingState.fromValue(brewingStep.toValue());
      mBrewingStep.setData(brewingStep.getData());
    }
  }

  /**
   * Gets the related brewing step.
   *
   * @return related brewing step which this confirmation is for
   */
  public BrewingState getBrewingStep() {
    return mBrewingStep;
  }

  /**
   * Sets the related brewing step which this confirmation is for.
   *
   * @param brewingStep the new brewing step
   */
  public void setBrewingStep(final BrewingState brewingStep) {
    mBrewingStep = brewingStep;
  }

}
