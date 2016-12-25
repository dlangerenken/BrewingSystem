/*
 *
 */
package messages;

import general.ManualStep;
import general.MessagePriority;


/**
 * Is send when a manual step has to be performed by the brewer.
 */
public class ManualStepMessage extends Message {

  /** The manual step object, containing all necessary data. */
  private ManualStep manualStep;

  /** Constructs a new ManualStepMessage without manualStep */
  public ManualStepMessage(final ManualStep step) {
    manualStep = step;
    setPriority(MessagePriority.ALWAYS);
  }

  /**
   * Gets the manual step.
   *
   * @return the manual step
   */
  public ManualStep getManualStep() {
    return manualStep;
  }

  /**
   * Sets the manual step.
   *
   * @param manualStep the new manual step
   */
  public void setManualStep(final ManualStep manualStep) {
    this.manualStep = manualStep;
  }
}
