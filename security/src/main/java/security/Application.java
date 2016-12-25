/*
 * 
 */
package security;

import modules.SecurityTestModule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;


/**
 * Entry-point for the Security Surveillance contains the main-method.
 *
 * @author Daniel Langerenken
 */
public class Application {

  /**
   * Main-Method of the program, starts the surveillance with the specific address from constants.
   *
   * @param args optional args which are ignored in this system
   */
  public static void main(final String[] args) {
    Injector injector = Guice.createInjector(new SecurityTestModule());
    Application application = injector.getInstance(Application.class);
    application.run(PropertyUtil.getServerAddress());
  }

  /** Logger which is used for logging. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** Security Surveillance which turns of the system in case of a missing response. */
  private final SecuritySurveillance securitySurveillance;

  /**
   * Creates the Application with a given Security Surveillance
   * 
   * @param securitySurveillance security surveillance which will run when application is started
   */
  @Inject
  public Application(final SecuritySurveillance securitySurveillance) {
    this.securitySurveillance = securitySurveillance;
  }

  /**
   * This method starts the security surveillance.
   * 
   * @param address Address of the server which should be observed
   */
  void run(final String address) {
    LOGGER.info("Security Surveillance started");
    securitySurveillance.start(address);
  }

  /**
   * This method stops the security surveillance.
   */
  void stop() {
    LOGGER.info("Security Surveillance stopped! Careful, the system is no longer observed");
    securitySurveillance.shutdownSystem();
  }


}
