/*
 *
 */
package exceptions;


/**
 * This exception is thrown if someone tried to interact with a protocol which does not exist.
 *
 * @author Daniel Langerenken
 */
public class ProtocolNotFoundException extends NotFoundException {

  /** Creates a {@link ProtocolNotFoundException} with nested exception */
  public ProtocolNotFoundException(final LogNotFoundException e) {
    super(e);
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2245564749865137781L;

}
