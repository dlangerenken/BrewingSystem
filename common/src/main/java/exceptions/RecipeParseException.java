/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if something inside of the RecipeReader/Writer was failing.
 *
 * @author Daniel Langerenken
 */
public class RecipeParseException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates the RecipeParseException with a nested exception.
   *
   * @param e nested Exception which should just have this wrapper around
   */
  public RecipeParseException(final Exception e) {
    super(e);
  }

  /**
   * Instantiates the RecipeParseException with a nested exception.
   *
   * @param e Exception message
   */
  public RecipeParseException(final String e) {
    super(e);
  }
}
