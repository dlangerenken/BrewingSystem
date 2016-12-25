package impl;

import interfaces.IAcousticNotifier;
import interfaces.ISignalControl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class simplifies the process of sending sound-notifications
 */
@Singleton
public class AcousticNotifier implements IAcousticNotifier {

  /** Controls the Raspberry's Pins */
  private final ISignalControl signaller;
  /** Whether to send PreNotification beeps */
  private final boolean doSendPreNotificationBeep;
  /** Whether to send ConfirmationRequest beeps */
  private final boolean doSendConfirmationRequestBeep;

  /**
   * Global logger for debugging purposes
   */
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Instantiates the AcousticNotifier by passing a SignalControl which can send out alarm sounds
   * 
   * @param signaller signal-control to send out notifications
   */
  @Inject
  public AcousticNotifier(final ISignalControl signaller) {
    this.signaller = signaller;
    this.doSendPreNotificationBeep = PropertyUtil.getSendPreNotificationBeep();
    this.doSendConfirmationRequestBeep = PropertyUtil.getSendConfirmationRequestBeep();
    LOGGER.info("AcousticNotifier initiated");
  }

  @Override
  public void sendPreNotificationBeep() {
    if (this.doSendPreNotificationBeep) {
      signaller.sendDoubleBeep();
      LOGGER.info("DoubleBeep sent");
    }
  }

  @Override
  public void sendConfirmationRequestBeep() {
    if (this.doSendConfirmationRequestBeep) {
      signaller.sendSingleBeep();
      LOGGER.info("SingleBeep sent");
    }
  }

  @Override
  public void switchOff() {
    signaller.switchOff();
  }

}
