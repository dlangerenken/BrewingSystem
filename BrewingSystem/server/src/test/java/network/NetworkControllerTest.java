package network;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;
import exceptions.ServerAlreadyRunningException;

/**
 * This class tests the network-controller
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class NetworkControllerTest {

  /**
   * controller which should be used in the test
   */
  private NetworkController controller;

  /** The exception which should occur in a few tests */
  @Rule
  public ExpectedException exception = ExpectedException.none();

  /**
   * Shutdown server after the test
   */
  @After
  public void after() {
    controller.shutdownServer();
  }

  /**
   * Starts the server before the tests
   * 
   * @throws ServerAlreadyRunningException should not occur
   */
  @Before
  public void before() throws ServerAlreadyRunningException {
    controller = new NetworkController();
    controller.startServer("0.0.0.0", 4242);
  }

  @Category(UnitTest.class)
  @Test
  public void testStartMultipleServerOnSamePort() throws ServerAlreadyRunningException {
    NetworkController mController = new NetworkController();
    mController.startServer("0.0.0.0", 4142);
    mController.shutdownServer();
    exception.expect(ServerAlreadyRunningException.class);
    mController.startServer("0.0.0.0", 4242);
    mController.shutdownServer();
  }
}
