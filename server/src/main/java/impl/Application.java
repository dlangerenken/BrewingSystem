/*
 * 
 */
package impl;

import interfaces.INetworkService;

import javax.inject.Inject;

import modules.BrewingModule;
import modules.BrewingTestModule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import exceptions.ServerAlreadyRunningException;


/**
 * This class contains the main-method and setups the injector, networkcontroller and UserFacade.
 *
 * @author Daniel Langerenken
 */
public class Application {
  /**
   * Returns the default-module-combination (All TestModules are overriden by the
   * main-brewing-module so that in case a main module is missing, the equivalent test is used)
   */
  public static final Module DEFAULT_MODULE = Modules.override(new BrewingTestModule()).with(
      new BrewingModule());

  /** The Guice-Injector for DependencyInjection which has a module as parameter. */
  private static Injector injector;

  /** Logger which is used for logging. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Returns the corresponding object
   * 
   * @param clazz clazz which is required
   * @return Object, if found
   */
  public static <T> T get(final Class<T> clazz) {
    return getInjector().getInstance(clazz);
  }

  /**
   * Returns the corresponding object (used for testing purposes)
   * 
   * @param key key which is required
   * @return Object, if found
   */
  public static <T> T get(final Key<T> key) {
    return getInjector().getInstance(key);
  }

  /**
   * Returns the injector (default-injector, if not set otherwise)
   * 
   * @return Injector to use within the application
   */
  private static Injector getInjector() {
    if (injector == null) {
      injector = Guice.createInjector(DEFAULT_MODULE);
    }
    return injector;
  }

  /**
   * The injector as singleton which is necessary for servlets to inject dependencies.
   * 
   * @param object object which should be injected
   */
  public static void inject(final Object object) {
    getInjector().injectMembers(object);
  }

  /**
   * Starting point of the application.
   *
   * @param args any arguments are ignored
   * @throws ServerAlreadyRunningException
   */
  public static void main(final String[] args) throws ServerAlreadyRunningException {
    Application application = getInjector().getInstance(Application.class);
    application.run();
  }

  /**
   * Sets the injector (needs to have this method - otherwise tests fail
   * 
   * @param injector Injector which should be used
   * @throws Exception exception if object already initiated
   */
  public static void setInjector(final Injector newInjector) throws Exception {
    if (injector == null) {
      injector = newInjector;
    } else {
      throw new Exception("Injector already initiated");
    }
  }

  /**
   * Sets the injector (needs to have this method - otherwise tests fail (Only use in tests where
   * appropriate!)
   * 
   * @param injector Injector which should be used
   * @param shouldInjectAnyway in case we need to set the injector even though it was set before
   * @throws Exception exception if object already initiated
   */
  public static void setInjector(final Injector newInjector, final boolean shouldInjectAnyway)
      throws Exception {
    if (shouldInjectAnyway) {
      injector = newInjector;
    } else {
      setInjector(injector);
    }
  }

  /** NetworkController for network-communication. */
  @Inject
  private INetworkService networkService;

  /**
   * Method which is called to start the brewing-system.
   * 
   * @throws ServerAlreadyRunningException Cannot start the server when another instance is already
   *         in use of the same port and address
   */
  private void run() throws ServerAlreadyRunningException {
    LOGGER.info("Application started");
    networkService.startServer();
  }

}
