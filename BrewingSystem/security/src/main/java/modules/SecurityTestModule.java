/*
 * 
 */
package modules;

import static org.mockito.Mockito.mock;
import interfaces.IHeaterControl;
import interfaces.ISignalControl;
import interfaces.IStirrerControl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;


/**
 * SecurityModule for Dependency Injection of the "test" objects.
 *
 * @author Daniel Langerenken
 */
public class SecurityTestModule extends AbstractModule {

  
  @Override
  protected void configure() {}

  /**
   * Returns a mock HeaterControl
   * 
   * @return mock heater control without any functionality
   */
  @Provides
  @Singleton
  public IHeaterControl getHeaterControl() {
    IHeaterControl service = mock(IHeaterControl.class);
    return service;
  }

  /**
   * Returns a mock StirrerControl
   * 
   * @return mock stirrer control without any functionality
   */
  @Provides
  @Singleton
  public IStirrerControl getStirrerControl() {
    IStirrerControl service = mock(IStirrerControl.class);
    return service;
  }

  /**
   * Returns a mock SignalControl
   * 
   * @return mock signal control without any functionality
   */
  @Provides
  @Singleton
  public ISignalControl getSignalControl() {
    ISignalControl service = mock(ISignalControl.class);
    return service;
  }
}
