package messages;

import general.MessagePriority;

/** A message to signal the start of the brewing of a recipe */
public class BrewingCompleteMessage extends Message {
  /** The name of the recipe */
  private final String recipeName;

  /** Creates a BrewingCompleteMessage for recipeName with very high priority */
  public BrewingCompleteMessage(final String recipeName) {
    setPriority(MessagePriority.VERY_HIGH);
    this.recipeName = recipeName;
  }

  /** Returns the recipe name */
  public String getRecipeName() {
    return recipeName;
  }

}
