/*
 * 
 */
package components;

import interfaces.IHeaterControl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.pi4j.io.gpio.RaspiPin;


/**
 * HeaterController which interacts with the hardware connected to the raspberry-pi.
 */
public class HeaterController extends RelaisController implements IHeaterControl {

  /** Global logger to log the interaction with the controller. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Instantiates the HeaterController and sets the correct pin.
   */
  public HeaterController() {
    /*
     * the pin is controlled inverse
     */
    super(PropertyUtil.HEATER_PIN_PROPERTY, RaspiPin.GPIO_03, "heater", true);
    LOGGER.info("HeaterController created");
  }

}
