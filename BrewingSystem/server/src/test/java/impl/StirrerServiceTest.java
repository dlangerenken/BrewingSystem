/*
 * 
 */
package impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import general.HardwareStatus;
import interfaces.IStirrerControl;
import interfaces.IStirrerService;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import categories.UnitTest;


/**
 * This class tests the stirrer service
 *
 * @author Daniel Langerenken
 */
@RunWith(MockitoJUnitRunner.class)
public class StirrerServiceTest {

  /**
   * StirrerControl which is used for this tests (just a mock)
   */
  private IStirrerControl stirrerControl;

  /** StirrerService instance which is reinitiated every test. */
  private IStirrerService stirrerService;

  /**
   * Inits the stirrerControl and service for every single test
   */
  @Before
  public void init() {
    stirrerControl = mock(IStirrerControl.class);
    stirrerService = new StirrerService(stirrerControl);
  }

  /**
   * Even though true should be returned, when an exception is thrown the status should be false
   */
  @Category(UnitTest.class)
  @Test
  public void testExceptions() {
    when(stirrerControl.isSwitchedOn()).thenReturn(true);
    Mockito.doThrow(new RuntimeException("Stirrer-Exception")).when(stirrerControl).switchOn();
    Assert.assertEquals(false, stirrerService.startStirring());

    Mockito.doThrow(new RuntimeException("Stirrer-Exception")).when(stirrerControl).switchOff();
    Assert.assertEquals(false, stirrerService.stopStirring());
  }

  /**
   * If you call start stirring the stirrerController should have called switchOn and isSwitchedOn
   */
  @Category(UnitTest.class)
  @Test
  public void testStartStirring() {
    when(stirrerControl.isSwitchedOn()).thenReturn(true);
    Assert.assertEquals(true, stirrerService.startStirring());

    verify(stirrerControl).switchOn();
    verify(stirrerControl).isSwitchedOn();
  }

  /**
   * Checks if the stirrer status is returned according to the switched-on result
   */
  @Category(UnitTest.class)
  @Test
  public void testStirrerStatus() {
    when(stirrerControl.isSwitchedOn()).thenReturn(true);
    Assert.assertTrue(stirrerService.getStatus() == HardwareStatus.ENABLED);
    when(stirrerControl.isSwitchedOn()).thenReturn(false);
    Assert.assertTrue(stirrerService.getStatus() == HardwareStatus.DISABLED);
  }

  /**
   * If you call stop stirring the stirrerController should have called switchOff and isSwitchedOn
   */
  @Category(UnitTest.class)
  @Test
  public void testStopStirring() {
    when(stirrerControl.isSwitchedOn()).thenReturn(false);
    Assert.assertEquals(true, stirrerService.stopStirring());

    verify(stirrerControl).switchOff();
    verify(stirrerControl).isSwitchedOn();
  }

}
