/*
 *
 */
package exceptions;


/**
 * This exception is thrown if a log could not be succesfully parsed.
 *
 * @author matthias
 */
public class LogParseException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new protocol parsing exception.
   *
   * @param e the exception that caused this exception to be thrown.
   */
  public LogParseException(final Exception e) {
    super(e);
  }

}
