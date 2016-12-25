/*
 * 
 */
package exceptions;


/**
 * Exception which is thrown in case someone tries to deal with an invalid brewing step (e.g.
 * confirmation of iodine-test too early)
 * 
 * @author Daniel Langerenken
 *
 */
public class InvalidBrewingStepException extends NotFoundException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2245564749865137781L;

  /**
   * Initiates the Exception with an optional given description.
   *
   * @param string optional description of the error
   */
  public InvalidBrewingStepException(final String string) {
    super(string);
  }

}
