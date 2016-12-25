/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if a protocol could not be succesfully read from file system.
 *
 * @author matthias
 */
public class ProtocolParsingException extends Exception {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new protocol parsing exception.
   *
   * @param e the e
   */
  public ProtocolParsingException(final Exception e) {
    super(e);
  }

  /**
   * Instantiates a new protocol parsing exception.
   *
   * @param s the s
   */
  public ProtocolParsingException(final String s) {
    super(s);
  }

}
