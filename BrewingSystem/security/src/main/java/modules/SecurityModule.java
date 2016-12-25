/*
 * 
 */
package modules;

import interfaces.IHeaterControl;
import interfaces.ISignalControl;
import interfaces.IStirrerControl;

import com.google.inject.AbstractModule;
import components.HeaterController;
import components.SignalController;
import components.StirrerController;


/**
 * SecurityModule for Dependency Injection of the "real" objects.
 *
 * @author Daniel Langerenken
 */
public class SecurityModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IStirrerControl.class).to(StirrerController.class);
    bind(IHeaterControl.class).to(HeaterController.class);
    bind(ISignalControl.class).to(SignalController.class);
  }

}
