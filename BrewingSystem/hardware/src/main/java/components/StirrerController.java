/*
 * 
 */
package components;

import interfaces.IStirrerControl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.pi4j.io.gpio.RaspiPin;


/**
 * StirrerController which interacts with the hardware connected to the raspberry-pi.
 */
public class StirrerController extends RelaisController implements IStirrerControl {

  /** Global logger to log the interaction with the controller. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Instantiates the StirrerController and sets the correct pin.
   */
  public StirrerController() {
    /*
     * the pin is controlled inverse
     */
    super(PropertyUtil.STIRRER_PIN_PROPERTY, RaspiPin.GPIO_04, "stirrer", true);
    LOGGER.info("StirrerController created");
  }

}
