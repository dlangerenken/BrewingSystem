/*
 * 
 */
package impl;

import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureEvent.SubscribeStatus;
import interfaces.ITemperatureService;
import junit.framework.Assert;
import mocks.MockHeatableThermometer;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.IntegrationTest;


/**
 * The Class TemperatureControllerTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class TemperatureControllerTest {


  class Verifier implements ITemperatureEvent {

    private int heatupTemp = 45;
    private boolean tempReached = false;
    private int tolerance = 2;


    @Override
    public SubscribeStatus temperatureReached(final float temperature) {
      // Assert that temperature is never too high.
      Assert.assertTrue(temperature < heatupTemp + tolerance);

      if (temperature > heatupTemp - tolerance) {
        Assert.assertFalse(temperature < heatupTemp - tolerance);
        tempReached = true;
      }

      return SubscribeStatus.SUBSCRIBE;
    }

  }

  /**
   * Tests if notifications are successfully sent
   */
  @Category(IntegrationTest.class)
  @Test
  public void testThatNotificationWorks() {

    // Initialize TemperatureController with a thermometer that can be heated up.
    MockHeatableThermometer heaterThermometer = new MockHeatableThermometer();
    ITemperatureService ftc = new impl.TemperatureController(heaterThermometer, heaterThermometer);

    ITemperatureEvent event = mock(ITemperatureEvent.class);

    when(event.temperatureReached(Mockito.anyInt())).thenReturn(SubscribeStatus.UNSUBSCRIBE);

    ftc.subscribe(39, 4, event);

    ftc.heatUp(39);

    // -> As heatup is randomized, 20 seconds should be enough so that 39° were reached once.
    verify(event, after(20 * 1000).atLeast(1)).temperatureReached(Mockito.anyInt());

    ftc.stop();
  }

  /**
   * Verifies that the temperature borders are used and not ignored
   */
  @Category(IntegrationTest.class)
  @Test
  public void testThatTemperatureBorderIsCorrect() {

    // Initialize TemperatureController with a thermometer that can be heated up.
    MockHeatableThermometer heaterThermometer = new MockHeatableThermometer();
    ITemperatureService ftc = new impl.TemperatureController(heaterThermometer, heaterThermometer);

    Verifier v = new Verifier();

    v.heatupTemp = 45;
    v.tolerance = 3;

    ftc.subscribe(0, 100, v);

    ftc.heatUp(v.heatupTemp);

    try {
      // Give it some time to work.....
      Thread.sleep(50 * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Assert.assertTrue(v.tempReached);

  }
}
