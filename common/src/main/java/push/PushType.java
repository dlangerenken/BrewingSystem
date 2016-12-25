/*
 * 
 */
package push;


/**
 * This enumeration shows the different push types the mobile client can receive and interact with.
 *
 * @author Daniel Langerenken
 */
public enum PushType {

  /** The test. */
  TEST("test"), /** The info. */
  INFO("info"), /** The alarm. */
  ALARM("alarm"), /** The message. */
  MESSAGE("message");

  /** Text which should be used for parsing instead of enum. */
  private final String text;

  /**
   * Instantiates a new push type.
   *
   * @param text the text
   */
  private PushType(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
};
