package network;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static utilities.NetworkRequestHelper.sendGet;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;

/**
 * This class provides tests for the actuator resource and checks that the network-request is
 * reassigned correctly
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ActuatorResourceTest extends ResourceControllerTestHelper {
  /**
   * Tests the actuator-resource of the network-communication
   * 
   * @throws Exception HttpRequest-Exception which can occur
   */
  @Category(UnitTest.class)
  @Test
  public void testActuator() throws Exception {
    sendGet(serverAddress + "actuator/");
    verify(userFacade, timeout(300).times(1)).getCurrentActuatorDetails();
  }
}
