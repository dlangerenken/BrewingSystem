/*
 *
 */
package exceptions;


/**
 * This exception is thrown if we try to start a server on a port which is already in use
 * 
 * @author Daniel Langerenken
 *
 */
public class ServerAlreadyRunningException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2245564749865137781L;

  /**
   * Initiates the exception without giving extra description.
   */
  public ServerAlreadyRunningException() {}

  /**
   * Initiates the exception with a error message (optional).
   *
   * @param string Optional error message
   */
  public ServerAlreadyRunningException(final String string) {
    super(string);
  }

  /** Creates a ServerAlreadyRunningException with nested exception */
  public ServerAlreadyRunningException(final Exception e) {
    super(e);
  }

}
