/*
 * 
 */
package exceptions;


/**
 * Exception which is thrown in case someone tries to operate on a not existing brewing process.
 *
 * @author Daniel Langerenken
 */
public class BrewingProcessNotFoundException extends NotFoundException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2245564749865137781L;

  /**
   * Initiates the Exception with an optional given description.
   *
   * @param string optional description of the error
   */
  public BrewingProcessNotFoundException(final String string) {
    super(string);
  }

}
