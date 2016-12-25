package modules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import general.MessagePriority;
import impl.BrewingPart;
import impl.HopCooker;
import impl.Masher;
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
import interfaces.IUserFacadeService;
import mocks.MockTemperatureController;

import org.mockito.Mockito;

import utilities.DummyBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
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
public class BrewingTestModule extends AbstractModule {


  private final IBrewingController brewingService = mock(IBrewingController.class);
  private final IGetRecipe getRecipeService = mock(IGetRecipe.class);
  private final IHeaterControl heaterService = mock(IHeaterControl.class);
  private final BrewingPart hopCookerService = mock(HopCooker.class);
  private final IBrewingLogService logService = mock(IBrewingLogService.class);
  private final BrewingPart masherService = mock(Masher.class);
  private final IMessageService messageService = mock(IMessageService.class);
  private final INetworkService networkService = mock(INetworkService.class);
  private final IProtocolService protocolService = mock(IProtocolService.class);
  private final IRecipeService recipeService = mock(IRecipeService.class);
  private final IRecipeStorage recipeStorage = mock(IRecipeStorage.class);
  private final IStirrerControl stirrerControl = mock(IStirrerControl.class);
  private final IStirrerService stirrerService = mock(IStirrerService.class);
  private final ILogStorage storateService = mock(ILogStorage.class);
  private final ITemperatureLogger temperatureLogger = mock(ITemperatureLogger.class);
  private final ITemperatureService temperatureService = new MockTemperatureController();
  private final IThermometerReader thermometerReader = mock(IThermometerReader.class);
  private final IUserFacadeService userFacadeService = mock(IUserFacadeService.class);

  /**
   * Instantiates the brewing test module
   */
  public BrewingTestModule() {
    initBrewingService();
    initLogService();
    initProtocolService();
    initGetRecipeService();
    initRecipeStorage();
    initRecipeService();
  }

  @Override
  protected void configure() {}

  /**
   * This method deals with the brewing and returns dummy objects.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception
   * @returns a mocked brewing service
   */
  @Provides
  public IBrewingController getBrewingService() throws BrewingProcessNotFoundException {
    return brewingService;
  }

  /**
   * This method deals with the heater control and returns dummy objects.
   *
   * @returns a mocked heater control
   */
  @Provides
  public IHeaterControl getHeaterControl() {
    return heaterService;
  }

  /**
   * This method deals with the HopCooker and returns dummy objects.
   *
   * @return the hop cooker
   */
  @Provides
  @Named("HopCooker")
  public BrewingPart getHopCooker() {
    return hopCookerService;
  }

  /**
   * This method deals with brewing logs and returns dummy objects.
   *
   * @throws BrewingProcessNotFoundException the brewing process not found exception
   * @returns a mocked brewing log service
   */
  @Provides
  public IBrewingLogService getLogService() throws BrewingProcessNotFoundException {
    return logService;
  }

  /**
   * This method deals with logs and returns dummy objects.
   *
   * @returns a mocked log storage
   */
  @Provides
  public ILogStorage getLogStorage() {
    return storateService;
  }

  /**
   * This method deals with the HopCooker and returns dummy objects.
   *
   * @return the hop cooker
   */
  @Provides
  @Named("Masher")
  public BrewingPart getMasher() {
    return masherService;
  }

  /**
   * This method deals with messages and returns dummy objects.
   *
   * @return a mocked message service
   */
  @Provides
  public IMessageService getMessageService() {
    return messageService;
  }

  /**
   * This method deals with the network service and returns dummy objects.
   *
   * @returns a mocked network service
   */
  @Provides
  public INetworkService getNetworkService() {
    return networkService;
  }

  /**
   * This method deals with protocols and returns dummy objects.
   *
   * @returns a mocked protocol service
   */
  @Provides
  public IProtocolService getProtocolService() {
    return protocolService;
  }

  /**
   * This method deals with recipes and returns dummy objects.
   *
   * @throws RecipeNotFoundException the recipe not found exception
   * @throws RecipeParseException the recipe parse exception
   * @returns a mocked get recipe service
   */
  @Provides
  public IGetRecipe getRecipeService() throws RecipeNotFoundException, RecipeParseException {
    return getRecipeService;
  }

  /**
   * This method deals with recipes and returns dummy objects.
   *
   * @throws RecipeNotFoundException the recipe not found exception
   * @throws RecipeParseException the recipe parse exception
   * @returns a mocked recipe storage
   */
  @Provides
  public IRecipeStorage getRecipeStorage() throws RecipeNotFoundException, RecipeParseException {
    return recipeStorage;
  }

  /**
   * This method deals with the stirrer control and returns dummy objects.
   *
   * @returns a mocked stirrer control
   */
  @Provides
  public IStirrerControl getStirrerControl() {
    return stirrerControl;
  }

  /**
   * This method deals with the stirrer service and returns dummy objects.
   *
   * @returns a mocked stirrer service
   */
  @Provides
  public IStirrerService getStirrerService() {
    return stirrerService;
  }

  /**
   * This method deals with the thermometer reader and returns dummy objects.
   *
   * @returns a mocked temperature reader
   */
  @Provides
  public IThermometerReader getTemperature() {
    return thermometerReader;
  }

  /**
   * This method deals with the temperature-logger
   *
   * @return a mocked temperature logger
   */
  @Provides
  public ITemperatureLogger getTemperatureLogger() {
    return temperatureLogger;
  }

  /**
   * This method deals with the temperature service and returns dummy objects.
   *
   * @returns a mocked temperature service
   */
  @Provides
  public ITemperatureService getTemperatureService() {
    return temperatureService;
  }

  /**
   * This method deals with the user facade and returns dummy objects.
   *
   * @returns a mocked user facade service
   */
  @Provides
  public IUserFacadeService getUserFacadeService() {
    return userFacadeService;
  }

  private void initBrewingService() {
    when(brewingService.getCurrentBrewingProcess()).thenReturn(DummyBuilder.getBrewingProcess());
    when(brewingService.getCurrentActuatorDetails()).thenReturn(DummyBuilder.getActuatorDetails());
  }

  private void initGetRecipeService() {
    try {
      when(getRecipeService.getRecipe(Mockito.anyString())).thenReturn(DummyBuilder.getRecipe());
      when(getRecipeService.getRecipe("weizenbock")).thenReturn(DummyBuilder.getRealisticRecipe());
    } catch (RecipeNotFoundException | RecipeParseException e1) {
    }
  }

  private void initLogService() {
    try {
      when(logService.getMessages()).thenReturn(DummyBuilder.getMessages());
      when(logService.getMessagesByPriority(Mockito.any(MessagePriority.class))).thenReturn(
          DummyBuilder.getMessages());
    } catch (BrewingProcessNotFoundException e1) {
    }
  }

  private void initProtocolService() {
    try {
      when(protocolService.getProtocolContent(Mockito.anyInt())).thenReturn(
          DummyBuilder.getProtocol());
    } catch (ProtocolNotFoundException | ProtocolParsingException e) {
    }
    when(protocolService.getProtocolIndex()).thenReturn(DummyBuilder.getProtocols());
  }

  private void initRecipeService() {
    try {
      when(recipeService.getRecipe(Mockito.anyString())).thenReturn(DummyBuilder.getRecipe());
      when(recipeService.getRecipe("weizenbock")).thenReturn(DummyBuilder.getRealisticRecipe());
      when(recipeService.selectRecipe()).thenReturn(DummyBuilder.getRecipes());
    } catch (RecipeNotFoundException | RecipeParseException e) {
    }
  }

  private void initRecipeStorage() {
    try {
      when(recipeStorage.getRecipe(Mockito.anyString())).thenReturn(DummyBuilder.getRecipe());
      when(recipeStorage.getRecipeSummaries()).thenReturn(DummyBuilder.getRecipes());
      when(recipeStorage.getRecipe("weizenbock")).thenReturn(DummyBuilder.getRealisticRecipe());
    } catch (RecipeNotFoundException | RecipeParseException e1) {
    }
  }

  /**
   * This method deals with recipes and returns dummy objects.
   *
   * @throws RecipeParseException the recipe parse exception
   * @returns a mocked recipe service
   */
  @Provides
  public IRecipeService recipeService() throws RecipeParseException {
    return recipeService;
  }
}
