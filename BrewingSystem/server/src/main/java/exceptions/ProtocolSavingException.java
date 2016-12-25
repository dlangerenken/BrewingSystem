/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if a protocol could not be succesfully saved to the file system.
 *
 * @author matthias
 */
public class ProtocolSavingException extends Exception {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new protocol saving exception.
   *
   * @param e the e
   */
  public ProtocolSavingException(final Exception e) {
    super(e);
  }

}
