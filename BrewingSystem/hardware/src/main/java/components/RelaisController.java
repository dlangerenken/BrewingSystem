/*
 * 
 */
package components;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


/**
 * RelaisController which interacts with the hardware connected to the raspberry-pi.
 */
public class RelaisController {

  /** Global logger to log the interaction with the controller. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** Pin which this controller is interacting with. */
  private final GpioPinDigitalOutput pin;

  /** Name of the pin. */
  private final String name;

  /**
   * Whether the pin states need to be inverted.
   * 
   * In our example, when controlling Relais: The pin state high means that the relais is switched
   * off and the pin state low means that the reials is switched on. This is called inverse
   * behavior.
   * 
   * However when controlling the beeper the behavior is not inverse!
   */
  private final boolean isInverse; // os220215

  /** The Constant pinMap. */
  protected static final Map<String, Pin> PIN_MAP;
  static {
    PIN_MAP = new HashMap<>();
    PIN_MAP.put("00", RaspiPin.GPIO_00);
    PIN_MAP.put("01", RaspiPin.GPIO_01);
    PIN_MAP.put("02", RaspiPin.GPIO_02);
    PIN_MAP.put("03", RaspiPin.GPIO_03);
    PIN_MAP.put("04", RaspiPin.GPIO_04);
    PIN_MAP.put("05", RaspiPin.GPIO_05);
    PIN_MAP.put("06", RaspiPin.GPIO_06);
    PIN_MAP.put("07", RaspiPin.GPIO_07);
    PIN_MAP.put("08", RaspiPin.GPIO_08);
    PIN_MAP.put("09", RaspiPin.GPIO_09);
    PIN_MAP.put("10", RaspiPin.GPIO_10);
    PIN_MAP.put("11", RaspiPin.GPIO_11);
    PIN_MAP.put("12", RaspiPin.GPIO_12);
    PIN_MAP.put("13", RaspiPin.GPIO_13);
    PIN_MAP.put("14", RaspiPin.GPIO_14);
    PIN_MAP.put("15", RaspiPin.GPIO_15);
    PIN_MAP.put("16", RaspiPin.GPIO_16);
    PIN_MAP.put("17", RaspiPin.GPIO_17);
    PIN_MAP.put("18", RaspiPin.GPIO_18);
    PIN_MAP.put("19", RaspiPin.GPIO_19);
    PIN_MAP.put("20", RaspiPin.GPIO_20);
  }

  /**
   * Creates a new RelaisController to interact with the specified pin. The initial state of this
   * pin will be HIGH.
   *
   * @param pin which should be interacted with
   * @param name Name of the pin (for logging purposes)
   * @param isInverse If true, the pinstate high means that the pin is switched off and vice versa.
   */
  public RelaisController(final String propertyName, final Pin defaultPin, final String name,
      final boolean isInverse) {
    this(propertyName, defaultPin, name, isInverse, PinState.HIGH);
  }

  /**
   * Creates a new RelaisController to interact with the specified pin. The initial state of this
   * pin will be HIGH.
   * 
   * @param propertyName The property from which to read the pin.
   * @param defaultPin The default pin to be used if no property exists.
   * @param name The name of the pin.
   * @param initialState The initial state of the pin.
   */
  public RelaisController(final String propertyName, final Pin defaultPin, final String name,
      final boolean isInverse, final PinState initialState) {
    /*
     * Get the GPI-Controller which contros the relais
     */
    GpioController gpio = GpioFactory.getInstance();
    // Initialize the pin
    this.pin = gpio.provisionDigitalOutputPin(getPin(propertyName, defaultPin), name, initialState);
    this.name = name;
    LOGGER.info(String.format("RelaisController for %s created.", name));

    this.isInverse = isInverse; // os220215
  }

  /**
   * Checks, whether the pin is enabled or disabled.
   *
   * @return boolean value which declares if the pin has the status HIGH (on) or LOW (off)
   */
  public boolean isSwitchedOn() {
    if (isInverse) { // os220215
      // Needs to be inverted.
      return pin.getState().isLow(); // os220215
    } else { // os220215
      return pin.getState().isHigh(); // os220215
    } // os220215
  }

  /**
   * Set the power for the pin to "low".
   */
  public void switchOff() {
    LOGGER.info(String.format("switchOff pin: %s", name));
    if (isInverse) { // os220215
      // needs to be inverted.
      pin.high(); // os220215
    } else { // os220215
      pin.low(); // os220215
    } // os220215
  }

  /**
   * Set the power for the pin to "high".
   */
  public void switchOn() {
    LOGGER.info(String.format("switchOn pin: %s", name));
    if (isInverse) { // os220215
      // needs to be inverted.
      pin.low(); // os220215
    } else { // os220215
      pin.high(); // os220215
    } // os220215
  }

  /**
   * Initializing the pin with a given property and otherwise a default-pin
   *
   * @return the pin
   */
  private static Pin getPin(final String property, final Pin defaultPin) {
    Pin pin = PIN_MAP.get(PropertyUtil.getProperty(property));
    if (pin == null) {
      pin = defaultPin;
    }
    return pin;
  }
}
