package utilities;

import interfaces.IUserFacadeService;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Provides a set method to change the object inside a provider
 * 
 * @author Daniel and Patrick
 *
 */
@Singleton
public class UserFacadeProvider implements Provider<IUserFacadeService> {

  /**
   * the service of this provider.
   */
  private IUserFacadeService service;

  @Override
  public IUserFacadeService get() {
    return service;
  }

  /**
   * Changes the intern service.
   * @param service
   */
  public void setUserFacade(final IUserFacadeService service) {
    this.service = service;
  }

}
