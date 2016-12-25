/*
 * 
 */
package network;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import junit.framework.Assert;
import network.push.Message.Builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import utilities.DummyBuilder;
import categories.UnitTest;

/**
 * This class tests if messages, which should be send to the client, are really send out from the
 * server to the subscribed clients.
 *
 * @author Daniel Langerenken
 */
@RunWith(MockitoJUnitRunner.class)
public class PushServiceTest {

  /**
   * The push-service which is used to send push-notifications to registered clients
   */
  private PushService pushService;

  /**
   * Initiates a new push-service every unit-test
   */
  @Before
  public void init() {
    pushService = spy(new PushService());
  }

  /**
   * Tests whether notifications are send out asynchronously or not (up from this point, google
   * "deals" with the notifications, so that we don't have to validate this)
   */
  @Category(UnitTest.class)
  @Test
  public void testNotifications() {
    for (int id = 0; id < 10; id++) {
      pushService.subscribe("myId:" + id);
    }
    pushService.notify(DummyBuilder.getMessage());
    verify(pushService, timeout(600).times(1)).asyncSend(Mockito.anyListOf(String.class),
        Mockito.any(Builder.class));
  }

  /**
   * tests whether clients can register at the service
   */
  @Category(UnitTest.class)
  @Test
  public void testSubscribe() {
    pushService.unsubscribe("1234");
    Assert.assertEquals(0, pushService.getConnectedDevices().size());

    pushService.subscribe("1234");
    Assert.assertEquals(1, pushService.getConnectedDevices().size());

    pushService.subscribe("1234");
    Assert.assertEquals(1, pushService.getConnectedDevices().size());

    pushService.subscribe("1235");
    Assert.assertEquals(2, pushService.getConnectedDevices().size());

    pushService.unsubscribe("1234");
    Assert.assertEquals(1, pushService.getConnectedDevices().size());

    pushService.unsubscribe("1234");
    Assert.assertEquals(1, pushService.getConnectedDevices().size());

    pushService.unsubscribe("1235");
    Assert.assertEquals(0, pushService.getConnectedDevices().size());
  }
}
