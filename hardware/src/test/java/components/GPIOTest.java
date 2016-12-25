/*
 * 
 */
package components;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import categories.IntegrationTest;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


/**
 * Class for testing heater and pump pin functionality.
 */
@RunWith(Parameterized.class)
public class GPIOTest {

  /**
   * Runs ten times with no real "values".
   *
   * @return List of times
   */
  @Parameterized.Parameters
  public static List<Object[]> data() {
    return Arrays.asList(new Object[10][0]);
  }

  /** The pin which is used for enabling and disabling of the heater. */
  private static GpioPinDigitalOutput heaterPin;

  /** The pin which is used for enabling and disabling of the pump. */
  private static GpioPinDigitalOutput pumpPin;

  /** The controller which gives the possibility to receive heater and pump pins. */
  private static GpioController gpio;

  /**
   * Parameterless constructor needed as test will start multiple times.
   */
  public GPIOTest() {}

  /**
   * stop all GPIO activity/threads by shutting down the GPIO controller (this method will
   * forcefully shutdown all GPIO monitoring threads and scheduled tasks).
   */
  @AfterClass
  public static void after() {
    heaterPin.high();
    pumpPin.high();
    gpio.shutdown();
  }

  /**
   * provision gpio pin #01 as an output pin and turn on.
   */
  @BeforeClass
  public static void init() {
    gpio = GpioFactory.getInstance();
    heaterPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "heating", PinState.HIGH);
    pumpPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "pump", PinState.HIGH);
  }

  /**
   * Smoke test that switches GPIO pins on and off.
   *
   * @throws InterruptedException if thread.sleep can't be executed
   */
  @Category(IntegrationTest.class)
  @Test
  public void testGpio() throws InterruptedException {
    heaterPin.low();
    pumpPin.low();
    Thread.sleep(1000);
    Assert.assertEquals(PinState.LOW, heaterPin.getState());
    Assert.assertEquals(PinState.LOW, pumpPin.getState());

    heaterPin.high();
    pumpPin.high();
    Thread.sleep(1000);
    Assert.assertEquals(PinState.HIGH, heaterPin.getState());
    Assert.assertEquals(PinState.HIGH, pumpPin.getState());
  }

}
