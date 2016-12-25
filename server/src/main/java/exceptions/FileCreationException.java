/*
 * 
 */
package exceptions;


/**
 * This exception is thrown if a file could not be succesfully created on the system.
 *
 * @author matthias
 */
public class FileCreationException extends Exception {
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new file creation exception.
   *
   * @param e the e
   */
  public FileCreationException(final Exception e) {
    super(e);
  }

}
