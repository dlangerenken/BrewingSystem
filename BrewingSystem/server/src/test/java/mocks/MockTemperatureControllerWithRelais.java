package mocks;

import com.google.inject.Singleton;
import components.HeaterController;

/**
 * TemperatureController which is mocked but still uses the relais on the raspberry pi
 *
 */
@Singleton
public class MockTemperatureControllerWithRelais extends MockTemperatureController {

  /**
   * Instantiates the mock-temperature-controller with relais
   */
  public MockTemperatureControllerWithRelais() {
    super(new MockHeatableThermometerWithRelais(new HeaterController()));
  }

}
