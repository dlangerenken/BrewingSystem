/*
 *
 */
package exceptions;


/**
 * This class or subclasses is thrown if an error related to the brewing system is made.
 *
 * @author Daniel Langerenken
 *
 */
public class BrewingProcessException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2245564749865137781L;

  /**
   * Initiates the exception without giving extra description.
   */
  public BrewingProcessException() {}

  /**
   * Initiates the exception with a error message (optional).
   *
   * @param string Optional error message
   */
  public BrewingProcessException(final String string) {
    super(string);
  }

  /** Creates a BrewingProcessException with nested exception */
  public BrewingProcessException(final Exception e) {
    super(e);
  }

}
