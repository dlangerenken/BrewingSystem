/*
 * 
 */
package components;

import interfaces.ISignalControl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


/**
 * SignalController which interacts with the hardware connected to the raspberry-pi.
 * 
 * @author Max
 */
public class SignalController extends RelaisController implements ISignalControl {

  /** Global logger to log the interaction with the controller. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * The duration of a short beep signal.
   */
  private static final long BEEP_DURATION_SHORT = 500;
  /**
   * The duration of a long beep signal.
   */
  private static final long BEEP_DURATION_LONG = 1000;

  /**
   * The pause between two beep signals.
   */
  private static final long BEEP_PAUSE = 500;

  /**
   * Instantiates the HeaterController and sets the correct pin.
   */
  public SignalController() {
    /* the pin is NOT controlled inverse! -> <- */
    super(PropertyUtil.SIGNAL_PIN_PROPERTY, RaspiPin.GPIO_05, "signal", false, PinState.LOW);
    LOGGER.info("SignalController created");
  }

  /**
   * Override the switchOn-method for security reasons, so that we can control that no one switches
   * on the beeper without switching it off.
   * 
   * The class internals use the swichOn method of the super class RelaisController.
   */
  @Override
  public void switchOn() {
    this.sendSingleBeep();
  }

  @Override
  public void sendDoubleBeep() {
    LOGGER.info("Send long double beep");

    Runnable doubleBeeper = new Runnable() {
      @Override
      public void run() {
        synchronized (SignalController.this) {
          try {
            sendBeep(BEEP_DURATION_LONG);
            Thread.sleep(BEEP_PAUSE);
            sendBeep(BEEP_DURATION_LONG);
          } catch (InterruptedException e) {
            switchOff();
            return;
          }
        }
      }
    };

    Thread asyncBeeper = new Thread(doubleBeeper);
    asyncBeeper.start();
  }

  @Override
  public void sendSingleBeep() {
    LOGGER.info("Send short single beep");

    Runnable singleBeeper = new Runnable() {
      @Override
      public void run() {
        synchronized (SignalController.this) {
          try {
            sendBeep(BEEP_DURATION_SHORT);
          } catch (InterruptedException e) {
            switchOff();
            return;
          }
        }
      }
    };

    Thread asyncBeeper = new Thread(singleBeeper);
    asyncBeeper.start();
  }

  private void sendBeep(final long duration) throws InterruptedException {
    super.switchOn();
    Thread.sleep(duration);
    super.switchOff();
  }

}
