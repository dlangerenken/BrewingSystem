package network;

import impl.Application;
import interfaces.INetworkService;
import interfaces.IUserFacadeService;
import modules.BrewingSpyModule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.NetworkRequestHelper;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

/**
 * This clas provides basic before and after implementation as well as a mocked user-interface which
 * can be used in every resource-test-class
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class ResourceControllerTestHelper {
  /**
   * The network-service which is started once in the beginning of this test-class
   */
  protected static INetworkService networkService;

  /**
   * Address we need to use
   */
  protected static String serverAddress;

  /**
   * Method which is called after executing all tests and which just shutdowns the server
   */
  @AfterClass
  public static void afterClass() {
    networkService.shutdownServer();
    networkService = null;
  }

  /**
   * Method which is called once for the complete test-series
   * 
   * @throws Exception if the injection fails this exception is thrown
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    Application.setInjector(Guice.createInjector(Modules.override(Application.DEFAULT_MODULE).with(
        new BrewingSpyModule())), true);
    networkService = Application.get(INetworkService.class);
    networkService.startServer(NetworkRequestHelper.ADDRESS, NetworkRequestHelper.PORT);
    serverAddress = NetworkRequestHelper.SERVER_ADDRESS;
  }

  /**
   * The user-facade which is called for every resource-call (and therefore can be tested)
   */
  protected IUserFacadeService userFacade;

  /**
   * Method which is called once every test
   */
  @Before
  public void before() {
    userFacade = Application.get(IUserFacadeService.class);
    Mockito.reset(userFacade);
  }
}
