package messages;

import general.MessagePriority;


/** A message to signal the start of the brewing of a recipe */
public class BrewingStartMessage extends Message {
  /** The name of the recipe */
  private final String recipeName;

  /** creates a BrewingStartMessage with recipeName and High priority */
  public BrewingStartMessage(final String recipeName) {
    setPriority(MessagePriority.HIGH);
    this.recipeName = recipeName;
  }

  /** Returns the recipe name */
  public String getRecipeName() {
    return recipeName;
  }

}
