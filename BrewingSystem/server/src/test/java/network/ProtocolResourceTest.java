package network;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static utilities.NetworkRequestHelper.sendGet;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;
import utilities.NetworkResult;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;

/**
 * This class provides tests for the protocol resource and checks that the network-request is
 * reassigned correctly
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProtocolResourceTest extends ResourceControllerTestHelper {

  /**
   * Checks if /protocols/XY/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocol() throws Exception {
    sendGet(serverAddress + "protocols/1/");
    verify(userFacade, timeout(300).times(1)).getProtocolContent(1);
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocolParsingException() throws Exception {
    Mockito.doThrow(ProtocolParsingException.class).when(userFacade).getProtocolContent(1);
    NetworkResult result = sendGet(serverAddress + "protocols/1/");
    verify(userFacade, timeout(300).times(1)).getProtocolContent(1);
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

  /**
   * Checks if /protocols/ is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocols() throws Exception {
    sendGet(serverAddress + "protocols/");
    verify(userFacade, timeout(300).times(1)).getProtocolIndex();
  }

  /**
   * Checks if a confirmation (where not needed) throws an exception to the client
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testProtocolThrowsNotFoundException() throws Exception {
    Mockito.doThrow(ProtocolNotFoundException.class).when(userFacade).getProtocolContent(1);
    NetworkResult result = sendGet(serverAddress + "protocols/1/");
    verify(userFacade, timeout(300).times(1)).getProtocolContent(1);
    Assert.assertEquals("Result was not negative", true, result.isNegativeResult());
  }

}
