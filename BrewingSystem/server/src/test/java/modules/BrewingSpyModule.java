/*
 * 
 */
package modules;

import static org.mockito.Mockito.mock;
import impl.UserFacade;
import interfaces.IMessageService;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;
import mocks.MockPushServiceWithAutoConfirm;
import mocks.MockTemperatureController;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Spy-Module for testing purposes.
 * 
 * @author Daniel Langerenken
 *
 */
public class BrewingSpyModule extends AbstractModule {


  @Override
  protected void configure() {}

  /**
   * This method returns a mocked message service which will reply to the server
   * 
   * @return
   */
  @Provides
  @Singleton
  public IMessageService getMessageService() {
    return new MockPushServiceWithAutoConfirm();
  }

  /**
   * This method deals with the temperature service and returns dummy objects.
   *
   * @return the temperature service
   * @returns a mocked temperature service
   */
  @Provides
  @Singleton
  public ITemperatureService getTemperatureService() {
    ITemperatureService service = new MockTemperatureController();
    return service;
  }

  /**
   * This method returns a mocked user-facade
   * 
   * @return the mocked user-facade
   */
  @Provides
  @Singleton
  public IUserFacadeService getUserFacade() {
    return mock(UserFacade.class);
  }
}
