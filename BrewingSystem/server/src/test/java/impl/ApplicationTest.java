package impl;

import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IBrewingService;
import interfaces.IGetRecipe;
import interfaces.ILogStorage;
import interfaces.IMessageService;
import interfaces.INetworkService;
import interfaces.IProtocolService;
import interfaces.IRecipeService;
import interfaces.IRecipeStorage;
import interfaces.IStirrerService;
import interfaces.ITemperatureEvent;
import interfaces.ITemperatureLogger;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;
import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import categories.UnitTest;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Tests that the Dependency Injection is set correctly within the deployment-module
 * 
 * @author Daniel Langerenken
 *
 */
public class ApplicationTest {

  /**
   * Resets the application-injector
   * 
   * @throws Exception should not occur
   * 
   */
  @AfterClass
  public static void afterClass() throws Exception {
    Application.setInjector(null, true);
  }

  /**
   * Injects the application with the default module
   * 
   * @throws Exception should not occur
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    Application.setInjector(Guice.createInjector(Application.DEFAULT_MODULE), true);
  }

  /**
   * ExpectedException which should occur in tests where the injection failsF
   */
  @Rule
  public ExpectedException exception = ExpectedException.none();

  /**
   * Validates if two instances created by dependency injection are the same
   * 
   * @param clazz class which should be created by injection
   * @param singleton if true, classes should be same, if false, classes need to be different
   */
  public <T> void assertIsSame(final Class<T> clazz, final boolean singleton) {
    T first = Application.get(clazz);
    T second = Application.get(clazz);
    if (singleton) {
      Assert.assertSame(first, second);
    } else {
      Assert.assertNotSame(first, second);
    }
  }

  /**
   * Verifies that the BrewingController is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyBrewingControllerIsSingleton() {
    assertIsSame(IBrewingController.class, true);
  }

  /**
   * Verifies that the BrewingLogService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyBrewingLogServiceIsSingleton() {
    assertIsSame(IBrewingLogService.class, true);
  }

  /**
   * Verifies that the BrewingPart is unique by name (Masher, HopCooker)
   */
  @Category(UnitTest.class)
  @Test
  public void verifyBrewingPartsAreSingletonByName() {
    BrewingPart masherFirst = Application.get(Key.get(BrewingPart.class, Names.named("Masher")));
    BrewingPart masherSecond = Application.get(Key.get(BrewingPart.class, Names.named("Masher")));
    Assert.assertSame(masherFirst, masherSecond);

    BrewingPart hopCookerFirst =
        Application.get(Key.get(BrewingPart.class, Names.named("HopCooker")));
    BrewingPart hopCookerSecond =
        Application.get(Key.get(BrewingPart.class, Names.named("HopCooker")));
    Assert.assertSame(hopCookerFirst, hopCookerSecond);

    Assert.assertNotSame(masherFirst, hopCookerFirst);
    Assert.assertNotSame(masherSecond, hopCookerSecond);
  }

  /**
   * Verifies that the BrewingService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyBrewingServiceIsSingleton() {
    assertIsSame(IBrewingService.class, true);
  }

  /**
   * Verifies that the IGetRecipe is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyGetRecipeIsSingleton() {
    assertIsSame(IGetRecipe.class, true);
  }

  /**
   * Verifies that the ILogStorage is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyLogStorageIsSingleton() {
    assertIsSame(ILogStorage.class, true);
  }

  /**
   * Verifies that the IMessageService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyMessageServiceIsSingleton() {
    assertIsSame(IMessageService.class, true);
  }

  /**
   * Verifies that the INetworkService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyNetworkServiceIsSingleton() {
    assertIsSame(INetworkService.class, true);
  }

  /**
   * Verifies that the IProtocolService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyProtocolServiceIsSingleton() {
    assertIsSame(IProtocolService.class, true);
  }

  /**
   * Verifies that the IRecipeService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyRecipeServiceIsSingleton() {
    assertIsSame(IRecipeService.class, true);
  }

  /**
   * Verifies that the IRecipeStorage is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyRecipeStorageIsSingleton() {
    assertIsSame(IRecipeStorage.class, true);
  }

  /**
   * Verifies that the IStirrerService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyStirrerService() {
    assertIsSame(IStirrerService.class, true);
  }

  /**
   * Verifies that the ITemperatureEvent cannot be injected
   */
  @Category(UnitTest.class)
  @Test
  public void verifyTemperatureEventIsNotInjected() {
    exception.expect(ConfigurationException.class);
    Application.get(ITemperatureEvent.class);
  }

  /**
   * Verifies that the ITemperatureLogger is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyTemperatureLoggerIsSingleton() {
    assertIsSame(ITemperatureLogger.class, true);
  }

  /**
   * Verifies that the ITemperatureService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyTemperatureServiceIsSingleton() {
    assertIsSame(ITemperatureService.class, true);
  }



  /**
   * Verifies that the IUserFacadeService is unique ("singleton")
   */
  @Category(UnitTest.class)
  @Test
  public void verifyUserFacadeIsSingleton() {
    assertIsSame(IUserFacadeService.class, true);
  }

}
