/*
 * 
 */
package security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import interfaces.IHeaterControl;
import interfaces.ISignalControl;
import interfaces.IStirrerControl;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.IntegrationTest;



/**
 * This class tests, if the security surveillance terminates heater/stirrer correctly.
 *
 * @author Daniel Langerenken
 */
@RunWith(MockitoJUnitRunner.class)
public class SecuritySurveillanceTest {

  /** Security surveillance for every test. */
  private SecuritySurveillance security;

  /** HeaterControl-Mock for tests. */
  private IHeaterControl mockHeater;

  /** StirrerControl-Mock for tests. */
  private IStirrerControl mockStirrer;

  /** SignalControl-Mock for tests. */
  private ISignalControl mockSignal;

  /**
   * Called for init the heater/stirrer and security for every test.
   */
  @Before
  public void init() {
    mockHeater = mock(IHeaterControl.class);
    mockStirrer = mock(IStirrerControl.class);
    mockSignal = mock(ISignalControl.class);
    security = spy(new SecuritySurveillance(mockHeater, mockStirrer, mockSignal));
    Mockito.reset(security);
  }

  /**
   * Shutsdown the security surveillance
   */
  @After
  public void after() {
    security.shutdownSystem();
  }

  /**
   * Makes sure that the ping-test works Requires internet
   */
  @Category(IntegrationTest.class)
  @Test
  public void pingTest() {
    security.start("http://www.google.de");
    verify(security, Mockito.after(20 * 1000).never()).isCancelled();
  }

  /**
   * Tests that the system will not throw any exception when started
   */
  @Category(IntegrationTest.class)
  @Test
  public void testApplicationRun() {
    when(security.ping("http://0.0.0.0:1234/alive", SecuritySurveillance.TIME_TO_AWAIT_RESPONSE))
        .thenReturn(true);
    Application application = new Application(security);
    application.run("http://0.0.0.0:1234/alive");
    verify(security, Mockito.after(1 * 1000).times(1)).runPingLoop("http://0.0.0.0:1234/alive");
    verify(security, Mockito.after(20 * 1000).never()).shutdownSystem();
    verify(mockHeater, never()).switchOff();
    verify(mockStirrer, never()).switchOff();
    verify(mockSignal, never()).switchOn();
    Assert.assertFalse(security.isCancelled());
    application.stop();
  }

  /**
   * Checks if the security surveillance does interrupt the system when the ping-tests are not
   * successful at all.
   */
  @Category(IntegrationTest.class)
  @Test
  public void testAlreadyFailedSystem() {
    /*
     * Should be false after initialization
     */
    Assert.assertFalse(security.isCancelled());

    security.start("this url is not valid, therefore the ping-result will be false");
    verify(security, timeout(20 * 1000).times(1)).shutdownSystem();
    verify(mockHeater).switchOff();
    verify(mockStirrer).switchOff();
    verify(mockSignal).switchOn();
    Assert.assertTrue(security.isCancelled());
  }

  /**
   * Checks if the security surveillance does interrupt the system when the ping-tests start to fail
   * after two successful pings.
   */
  @Category(IntegrationTest.class)
  @Test
  public void testFailingSystem() {
    String address = "this url is not valid, therefore the ping-result will be false";
    when(security.ping(address, SecuritySurveillance.TIME_TO_AWAIT_RESPONSE)).thenReturn(true)
        .thenReturn(true).thenReturn(false);
    /*
     * Should be false after initialization
     */
    Assert.assertFalse(security.isCancelled());
    security.start("this url is not valid, therefore the ping-result will be false");
    verify(security, timeout(30 * 1000).times(1)).shutdownSystem();
    verify(mockHeater).switchOff();
    verify(mockStirrer).switchOff();
    verify(mockSignal).switchOn();
    Assert.assertTrue(security.isCancelled());
  }

  /**
   * Checks if the security surveillance does not interrupt the system when the ping-tests are
   * successful at any time.
   */
  @Category(IntegrationTest.class)
  @Test
  public void testRunningSystem() {
    when(security.ping("http://0.0.0.0:1234/alive", SecuritySurveillance.TIME_TO_AWAIT_RESPONSE))
        .thenReturn(true);
    /*
     * Should be false after initialization
     */
    Assert.assertFalse(security.isCancelled());

    security.start("http://0.0.0.0:1234/alive");
    verify(security, Mockito.after(1 * 1000).times(1)).runPingLoop("http://0.0.0.0:1234/alive");
    verify(security, Mockito.after(20 * 1000).never()).shutdownSystem();
    verify(mockHeater, never()).switchOff();
    verify(mockStirrer, never()).switchOff();
    verify(mockSignal, never()).switchOn();
    Assert.assertFalse(security.isCancelled());
  }
}
