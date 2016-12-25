package mocks;

import interfaces.IAcousticNotifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mocked Acousticotifier which doesn't do anything but logging
 */
public class MockAcousticNotifier implements IAcousticNotifier {

  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Instantiates the mock
   */
  public MockAcousticNotifier() {
    LOGGER.info("MockAcousticNotifier constructed");
  }

  @Override
  public void sendPreNotificationBeep() {
    LOGGER.info("BEEP--BEEP for Pre-Notification");
  }

  @Override
  public void sendConfirmationRequestBeep() {
    LOGGER.info("BEEEEP for Confirmation Request");
  }

  @Override
  public void switchOff() {
    LOGGER.info("BEEEP explicitely switched off");
  }

}
