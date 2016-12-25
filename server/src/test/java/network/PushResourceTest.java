package network;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static utilities.NetworkRequestHelper.sendGet;
import static utilities.NetworkRequestHelper.sendPost;
import messages.Message;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import push.PushType;
import categories.UnitTest;

/**
 * This class provides tests for the push resource and checks that the network-request is reassigned
 * correctly
 * 
 * @author Daniel Langerenken
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PushResourceTest extends ResourceControllerTestHelper {

  /**
   * Checks if /push/notify?message=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testNotify() throws Exception {
    sendGet(serverAddress + "push/notify?message=hallowelt");
    verify(userFacade, timeout(300).times(1)).notify(Mockito.any(Message.class));
  }

  /**
   * Checks if /push/push?type=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testPush() throws Exception {
    sendGet(serverAddress + "push/push?type=1");
    verify(userFacade, timeout(300).times(1)).notify(Mockito.anyString(),
        Mockito.any(PushType.class));
  }

  /**
   * Checks if /push/subscribe?regId=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testSubscribe() throws Exception {
    sendPost(serverAddress + "push/subscribe", "regId=1234");
    verify(userFacade, timeout(300).times(1)).subscribe("1234");
  }

  /**
   * Checks if /push/unsubscribe?regId=XY is handled correctly
   * 
   * @throws Exception
   */
  @Category(UnitTest.class)
  @Test
  public void testUnsubscribe() throws Exception {
    sendPost(serverAddress + "push/unsubscribe", "regId=1234");
    verify(userFacade, timeout(300).times(1)).unsubscribe("1234");
  }
}
