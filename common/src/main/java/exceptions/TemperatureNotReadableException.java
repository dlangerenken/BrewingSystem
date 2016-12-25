/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if something inside of the TemperatureReader failed.
 *
 * @author Daniel Langerenken
 */
public class TemperatureNotReadableException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates the TemperatureNotReadableException with a nested exception.
   *
   * @param e nested Exception which should just have this wrapper around
   */
  public TemperatureNotReadableException(final Throwable e) {
    super(e);
  }
}
