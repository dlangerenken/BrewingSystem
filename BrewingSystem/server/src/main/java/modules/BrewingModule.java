/*
 *
 */
package modules;

import impl.BrewingController;
import impl.BrewingLogger;
import impl.BrewingPart;
import impl.HopCooker;
import impl.Masher;
import impl.ProtocolManagement;
import impl.RecipeManagement;
import impl.StirrerService;
import impl.TemperatureLogger;
import impl.UserFacade;
import interfaces.IAcousticNotifier;
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
import interfaces.ITemperatureLogger;
import interfaces.ITemperatureService;
import interfaces.IUserFacadeService;

import java.lang.annotation.Annotation;

import mocks.MockAcousticNotifier;
import mocks.MockTemperatureController;
import network.NetworkController;
import network.PushService;
import persistence.PersistenceHandler;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


/**
 * Main-Module to use. Comment out a binding, when you would like to use the equivalent mock of this
 * interface
 *
 * @author Daniel Langerenken
 *
 */
public class BrewingModule extends AbstractModule {


  @Override
  protected void configure() {
    bind(IRecipeService.class).to(RecipeManagement.class);
    bind(IBrewingService.class).to(BrewingController.class);
    bind(IBrewingController.class).to(BrewingController.class);
    bind(IBrewingLogService.class).to(BrewingLogger.class);
    bind(IGetRecipe.class).to(RecipeManagement.class);
    bind(ITemperatureService.class).to(MockTemperatureController.class);
    bind(IStirrerService.class).to(StirrerService.class);
    bind(INetworkService.class).to(NetworkController.class);
    bind(IMessageService.class).to(PushService.class);
    bind(IRecipeStorage.class).to(PersistenceHandler.class);
    bind(ILogStorage.class).to(PersistenceHandler.class);
    bind(IUserFacadeService.class).to(UserFacade.class);
    bind(ITemperatureLogger.class).to(TemperatureLogger.class);
    bind(IProtocolService.class).to(ProtocolManagement.class);
    // bind(IHeaterControl.class).to(HeaterController.class);
    // bind(IStirrerControl.class).to(components.StirrerController.class);
    // bind(IThermometerReader.class).to(components.ThermometerReader.class);
    // bind(ISignalControl.class).to(components.SignalController.class);
    // bind(IAcousticNotifier.class).to(AcousticNotifier.class);
    bind(IAcousticNotifier.class).to(MockAcousticNotifier.class);
    
    /*
     * both HopCooker and Masher are from type BrewingPart - thus we need to differ them by using
     * annotations
     */
    Annotation hopCookerAnnotation = Names.named("HopCooker");
    Annotation masherAnnotation = Names.named("Masher");
    bind(BrewingPart.class).annotatedWith(hopCookerAnnotation).to(HopCooker.class);
    bind(BrewingPart.class).annotatedWith(masherAnnotation).to(Masher.class);
  }
}
