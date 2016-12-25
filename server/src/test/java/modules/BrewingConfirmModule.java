/*
 * 
 */
package modules;

import interfaces.IMessageService;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;
import mocks.MockPushServiceWithAutoConfirm;
import mocks.MockTemperatureController;
import utilities.UserFacadeProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Spy-Module for testing purposes.
 * 
 * @author Daniel Langerenken
 *
 */
public class BrewingConfirmModule extends AbstractModule {

  private static UserFacadeProvider awesomeProvider = new UserFacadeProvider();

  private final IMessageService pushService = new MockPushServiceWithAutoConfirm();
  private final ITemperatureService service = new MockTemperatureController();
  
  @Override
  protected void configure() {}

  @Provides
  public UserFacadeProvider getAwesomeProvider() {
    return awesomeProvider;
  }

  /**
   * This method returns a mocked message service which will reply to the server
   * 
   * @return
   */
  @Provides
  public IMessageService getMessageService() {
    return pushService;
  }

  /**
   * This method deals with the temperature service and returns dummy objects.
   *
   * @return the temperature service
   * @returns a mocked temperature service
   */
  @Provides
  public ITemperatureService getTemperatureService() {
    return service;
  }

  /**
   * This method returns a mocked user-facade
   * 
   * @return the mocked user-facade
   */
  @Provides
  public IUserFacadeService getUserFacade() {
    return awesomeProvider.get();
  }
}
