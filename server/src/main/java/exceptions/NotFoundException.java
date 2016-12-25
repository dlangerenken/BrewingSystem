/*
 *
 */
package exceptions;


/**
 * Exception which is thrown if an operation was executed for a non existing object.
 *
 * @author Daniel Langerenken
 */
public class NotFoundException extends BrewingProcessException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2245564749865137781L;

  /**
   * Instantiates the exception without an optional description.
   */
  public NotFoundException() {}

  /**
   * Instantiates the exception with an optional description.
   *
   * @param string Optional description of the exception
   */
  public NotFoundException(final String string) {
    super(string);
  }

  /** Creates a NotFoundException with Nested Exception */
  public NotFoundException(final Exception e) {
    super(e);
  }

}
