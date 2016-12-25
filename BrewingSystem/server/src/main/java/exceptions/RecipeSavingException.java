/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if a recipe could not be succesfully saved to the file system.
 *
 * @author matthias
 */
public class RecipeSavingException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new recipe saving exception.
   *
   * @param e the e
   */
  public RecipeSavingException(final Exception e) {
    super(e);
  }

  /**
   * Instantiates a new recipe saving exception.
   *
   * @param msg the error message
   */
  public RecipeSavingException(final String msg) {
    super(msg);
  }

}
