package mocks;


import interfaces.IHeaterControl;

/**
 * Provides the same functionality as the MockHeatableThermometer but also controlles the real
 * relais to show that we can do it.
 * 
 * @author max
 *
 */
public class MockHeatableThermometerWithRelais extends MockHeatableThermometer {

  private final IHeaterControl heaterControl;

  /**
   * Instantiates the mockheatable-thermometer with relais
   * 
   * @param heaterController
   */
  public MockHeatableThermometerWithRelais(final IHeaterControl heaterController) {
    super();
    this.heaterControl = heaterController;
  }

  @Override
  public void switchOn() {
    // Switch on Real relais.
    this.heaterControl.switchOn();
    // .. and then propagate it to the mock:
    super.switchOn();
  }

  @Override
  public void switchOff() {
    // Switch of real relais:
    this.heaterControl.switchOff();
    // .. and then propagate it to the mock:
    super.switchOff();
  }
}
