/*
 *
 */
package modules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import impl.BrewingController;
import impl.BrewingPart;
import impl.HopCooker;
import impl.Masher;
import impl.TemperatureLogger;
import interfaces.IAcousticNotifier;
import interfaces.IBrewingController;
import interfaces.IBrewingLogService;
import interfaces.IGetRecipe;
import interfaces.IHeaterControl;
import interfaces.ILogStorage;
import interfaces.IMessageService;
import interfaces.INetworkService;
import interfaces.IProtocolService;
import interfaces.IRecipeService;
import interfaces.IRecipeStorage;
import interfaces.IStirrerControl;
import interfaces.IStirrerService;
import interfaces.ITemperatureLogger;
import interfaces.ITemperatureService;
import interfaces.IThermometerReader;
import mocks.MockTemperatureController;

import org.mockito.Mockito;

import persistence.PersistenceHandler;
import utilities.UserFacadeProvider;
import utilities.DummyBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import exceptions.BrewingProcessNotFoundException;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import exceptions.RecipeNotFoundException;
import exceptions.RecipeParseException;


/**
 * Mock module which returns mocked objects instead of a "real" implementation.
 *
 * @author Daniel Langerneken
 */
public class BrewingControllerTestModule extends AbstractModule {
  private static UserFacadeProvider userFacadeProvider = new UserFacadeProvider();

  static ITemperatureService temperatureService = new MockTemperatureController();

  IBrewingController bc = new BrewingController(getTemperatureService(), getLogService(),
      getStirrerService(), getMasher(), getHopCooker(), getTemperatureLogger(), getAcousticNotifier(),
      getAwesomeProvider(), new PersistenceHandler());

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public UserFacadeProvider getAwesomeProvider() {
    return userFacadeProvider;
  }

  /**
   * Returns a brewing controller provider which can be changed dynamically
   * @return brewing-controller provider
   */
  public Provider<IBrewingController> getBrewingControllerProvider() {
    Provider<IBrewingController> provider = new Provider<IBrewingController>() {

      @Override
      public IBrewingController get() {
        return getBrewingService();
      }
    };
    return provider;
  }

  /**
   * This method deals with the brewing and returns dummy objects.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception
   * @returns a mocked brewing service
   */
  @Provides
  @Singleton
  public IBrewingController getBrewingService() {
    return bc;
  }

  /**
   * This method deals with the heater control and returns dummy objects.
   *
   * @returns a mocked heater control
   */
  @Provides
  @Singleton
  public IHeaterControl getHeaterControl() {
    IHeaterControl service = mock(IHeaterControl.class);
    return service;
  }
  
  /* Begin os220215 { */
  /**
   * Gets the acoustic notifier as dummy object.
   * @return
   */
  @Provides
  @Singleton
  public IAcousticNotifier getAcousticNotifier() {
  	return mock(IAcousticNotifier.class);
  }
  /* End os220215  }*/
  
  /**
   * This method deals with the HopCooker and returns dummy objects.
   *
   * @return the hop cooker
   */
  @Provides
  @Singleton
  @Named("HopCooker")
  public BrewingPart getHopCooker() {
    BrewingPart part =
        new HopCooker(getTemperatureService(), getLogService(), getBrewingControllerProvider());
    return part;
  }

  /**
   * This method deals with brewing logs and returns dummy objects.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception
   * @returns a mocked brewing log service
   */
  @Provides
  @Singleton
  public IBrewingLogService getLogService() {
    IBrewingLogService service = mock(IBrewingLogService.class);
    return service;
  }

  /**
   * This method deals with logs and returns dummy objects.
   *
   * @returns a mocked log storage
   */
  @Provides
  @Singleton
  public ILogStorage getLogStorage() {
    ILogStorage service = mock(ILogStorage.class);
    return service;
  }

  /**
   * This method deals with the HopCooker and returns dummy objects.
   *
   * @return the hop cooker
   */
  @Provides
  @Singleton
  @Named("Masher")
  public BrewingPart getMasher() {
    BrewingPart part =
        new Masher(getTemperatureService(), getStirrerService(), getLogService(),
            getBrewingControllerProvider());
    return part;
  }

  /**
   * This method deals with messages and returns dummy objects.
   *
   * @return a mocked message service
   */
  @Provides
  @Singleton
  public IMessageService getMessageService() {
    IMessageService service = mock(IMessageService.class);
    return service;
  }


  /**
   * This method deals with the network service and returns dummy objects.
   *
   * @returns a mocked network service
   */
  @Provides
  @Singleton
  public INetworkService getNetworkService() {
    INetworkService service = mock(INetworkService.class);
    return service;
  }


  /**
   * This method deals with protocols and returns dummy objects.
   *
   * @returns a mocked protocol service
   */
  @Provides
  @Singleton
  public IProtocolService getProtocolService() {
    IProtocolService service = mock(IProtocolService.class);
    try {
      when(service.getProtocolContent(Mockito.anyInt())).thenReturn(DummyBuilder.getProtocol());
    } catch (ProtocolNotFoundException | ProtocolParsingException e) {
      e.printStackTrace();
    }
    when(service.getProtocolIndex()).thenReturn(DummyBuilder.getProtocols());
    return service;
  }

  /**
   * This method deals with recipes and returns dummy objects.
   *
   * @throws RecipeNotFoundException the recipe not found exception
   * @throws RecipeParseException the recipe parse exception
   * @returns a mocked get recipe service
   */
  @Provides
  @Singleton
  public IGetRecipe getRecipeService() throws RecipeNotFoundException, RecipeParseException {
    IGetRecipe service = mock(IGetRecipe.class);
    when(service.getRecipe(Mockito.anyString())).thenReturn(DummyBuilder.getRecipe());
    when(service.getRecipe("weizenbock")).thenReturn(DummyBuilder.getRealisticRecipe());
    return service;
  }

  /**
   * This method deals with recipes and returns dummy objects.
   *
   * @throws RecipeNotFoundException the recipe not found exception
   * @throws RecipeParseException the recipe parse exception
   * @returns a mocked recipe storage
   */
  @Provides
  @Singleton
  public IRecipeStorage getRecipeStorage() throws RecipeNotFoundException, RecipeParseException {
    IRecipeStorage service = mock(IRecipeStorage.class);
    when(service.getRecipe(Mockito.anyString())).thenReturn(DummyBuilder.getRecipe());
    when(service.getRecipeSummaries()).thenReturn(DummyBuilder.getRecipes());
    when(service.getRecipe("weizenbock")).thenReturn(DummyBuilder.getRealisticRecipe());
    return service;
  }

  /**
   * This method deals with the stirrer control and returns dummy objects.
   *
   * @returns a mocked stirrer control
   */
  @Provides
  @Singleton
  public IStirrerControl getStirrerControl() {
    IStirrerControl service = mock(IStirrerControl.class);
    return service;
  }

  /**
   * This method deals with the stirrer service and returns dummy objects.
   *
   * @returns a mocked stirrer service
   */
  @Provides
  @Singleton
  public IStirrerService getStirrerService() {
    IStirrerService service = mock(IStirrerService.class);
    return service;
  }

  /**
   * This method deals with the thermometer reader and returns dummy objects.
   *
   * @returns a mocked temperature reader
   */
  @Provides
  @Singleton
  public IThermometerReader getTemperature() {
    IThermometerReader service = mock(IThermometerReader.class);
    return service;
  }

  /**
   * This method deals with the temperature-logger
   *
   * @return a mocked temperature logger
   * @throws BrewingProcessNotFoundException
   */
  @Provides
  public ITemperatureLogger getTemperatureLogger() {
    ITemperatureLogger service = new TemperatureLogger(getLogService());
    return service;
  }

  /**
   * This method deals with the temperature service and returns dummy objects.
   *
   * @returns a mocked temperature service
   */
  @Provides
  @Singleton
  public ITemperatureService getTemperatureService() {
    return temperatureService;
  }

  /**
   * This method deals with recipes and returns dummy objects.
   *
   * @throws RecipeParseException the recipe parse exception
   * @returns a mocked recipe service
   */
  @Provides
  @Singleton
  public IRecipeService recipeService() throws RecipeParseException {
    IRecipeService service = mock(IRecipeService.class);
    try {
      when(service.getRecipe(Mockito.anyString())).thenReturn(DummyBuilder.getRecipe());
      when(service.getRecipe("weizenbock")).thenReturn(DummyBuilder.getRealisticRecipe());
    } catch (RecipeNotFoundException e) {
      e.printStackTrace();
    }
    when(service.selectRecipe()).thenReturn(DummyBuilder.getRecipes());
    return service;
  }
}
