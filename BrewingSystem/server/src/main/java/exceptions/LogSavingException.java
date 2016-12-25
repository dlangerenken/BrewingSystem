/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if a log could not be succesfully saved to the file system.
 *
 * @author matthias
 */
public class LogSavingException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new log saving exception.
   *
   * @param e the e
   */
  public LogSavingException(final Exception e) {
    super(e);
  }

  /**
   * Instantiates a new log saving exception with a description
   * 
   * @param string
   */
  public LogSavingException(final String string) {
    super(string);
  }

}
