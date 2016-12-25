/*
 * 
 */
package mocks;

import general.HardwareStatus;
import impl.TemperatureController;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Singleton;


/**
 * The MockTemperatureController which is a Proxy for the normal TemperatureController that is
 * constructed with a MockHeatableThermometer.
 */
@Singleton
public class MockTemperatureController implements ITemperatureService {

  private static final Logger LOGGER = LogManager.getLogger();
  private Float lastHeatupTemperature;
  private final ITemperatureService temperatureService;
  
  private final MockHeatableThermometer thermo;
  
  /**
   * Constructs a new MockTemperatureController.
   */
  public MockTemperatureController() {
    this(new MockHeatableThermometer());
  }
  
  /**
   * Constructs a new MockTemperatureController and allows to manually
   * specify the MockHeatableThermometer to be used.
   * @param thermo
   */
  public MockTemperatureController(final MockHeatableThermometer thermo) {
  	LOGGER.info("MOCK: Constructed MOCK TemperatureController");
  	this.thermo = thermo;
  	this.temperatureService = new TemperatureController(this.thermo, this.thermo);
    this.lastHeatupTemperature = null;
  }
  
  /**
   * Constructs a new MockTemperatureController and sets instantHeatup to the
   * specified value.
   * @param autoHeatup
   */
  public MockTemperatureController(final boolean instantHeatup) {
    this();
    this.thermo.setInstantHeatup(instantHeatup, null);
  }
  
  @Override
  public HardwareStatus getHeaterStatus() {
    if (thermo.isSwitchedOn()) {
    	return HardwareStatus.ENABLED;
    }
    else {
    	return HardwareStatus.DISABLED;
    }
  }
  
  @Override
  public Float getTemperature() {
    return temperatureService.getTemperature();
  }
  
  @Override
  public HardwareStatus getTemperatureSensorStatus() {
    return HardwareStatus.ENABLED;
  }

  @Override
  public void heatUp(final int temperature) {
    if (this.thermo.getInstantHeatup()) {
      // If instant heatup is enabled then supply the temperature.
      this.thermo.setInstantHeatup(true, (float) temperature);
    }
    this.lastHeatupTemperature = (float)temperature;
    this.temperatureService.heatUp(temperature);
  }

  /**
   * Gets whether instant heatup is enabled or not.
   * @return
   */
  public boolean isInstantHeatUp() {
    return thermo.getInstantHeatup();
  }

  /**
   * If set to true, the temperature supplied in HeatUp will be simulated to be reached
   * immediately.
   * @param value
   */
  public void setInstantHeatUp(final boolean value) {
    // Enable instant heatup on the thermometer, but as we do not yet know the
    // desired target temperature, initialize the temperature with null and
    // actualize it when heatUp() is called.
    thermo.setInstantHeatup(value, this.lastHeatupTemperature);
  }

  @Override
  public void stop() {
    this.temperatureService.stop();
  }

  @Override
  public void subscribe(final int temperature, final int delta, final ITemperatureEvent notify) {
    this.temperatureService.subscribe(temperature, delta, notify);
  }

  @Override
  public void unsubscribe(final ITemperatureEvent notify) {
    this.temperatureService.unsubscribe(notify);
  }
  
}
