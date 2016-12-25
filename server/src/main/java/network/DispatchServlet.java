/*
 * 
 */
package network;

import gson.Serializer;

import java.io.IOException;
import java.rmi.UnexpectedException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dispatcher.Dispatcher;
import dispatcher.HttpMethod;
import dispatcher.ResourceHandler;


/**
 * This Dispatcher bases on a framework by Helmar Hutschenreuter (University of Bremen). Every
 * Resource is defined with methods and parameters it should receive and so the REST-pattern is
 * implemented in a very easy way
 */
@SuppressWarnings("serial")
public class DispatchServlet extends HttpServlet {

  /** Dispatcher which redirects every request to the appropriate class and method. */
  private Dispatcher dispatcher;

  /** Global instance of the Logger for logging purposes. */
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Creates the brewing-resource which deals with everything about the brewing process (e.g. status
   * of brewing, temperature...)
   */
  private void createActuatorResources() {
    ResourceHandler actuatorHandler =
        dispatcher.registerResource("/actuator/", ActuatorResource.class);
    actuatorHandler.registerRoutine(HttpMethod.GET, "", "info");
  }

  /**
   * Creates the brewing-resource which deals with everything about the brewing process (e.g. status
   * of brewing, temperature...)
   */
  private void createBrewingResources() {
    ResourceHandler brewingHandler =
        dispatcher.registerResource("/brewing/", BrewingResource.class);
    brewingHandler.registerRoutine(HttpMethod.GET, "state", "state");
    brewingHandler.registerRoutine(HttpMethod.GET, "summary", "summary");
    brewingHandler.registerRoutine(HttpMethod.GET, "", "info");
    brewingHandler.registerRoutine(HttpMethod.POST, "confirm", new String[] {"state"},
        new Class<?>[] {String.class}, "confirmStep");
    brewingHandler.registerRoutine(HttpMethod.POST, "iodine", new String[] {"duration"},
        new Class<?>[] {Integer.class}, "iodineTest");
    brewingHandler.registerRoutine(HttpMethod.POST, "start", new String[] {"recipeId"},
        new Class<?>[] {String.class}, "startBrewing");
    brewingHandler.registerRoutine(HttpMethod.POST, "temperature", new String[] {"since"},
        new Class<?>[] {Long.class}, "temperatureHistory");
    brewingHandler.registerRoutine(HttpMethod.POST, "messages", new String[] {"since"},
        new Class<?>[] {Long.class}, "messageHistory");
    brewingHandler.registerRoutine(HttpMethod.POST, "cancel", "cancel");
    brewingHandler.registerRoutine(HttpMethod.GET, "push", "pushMessages");
  }

  /**
   * Creates the info-resource which shows different information (e.g. runtime, current brewing
   * process)
   */
  private void createInfoResources() {
    ResourceHandler infoHandler = dispatcher.registerResource("/", InfoResource.class);
    infoHandler.registerRoutine(HttpMethod.GET, "", "info");
    infoHandler.registerRoutine(HttpMethod.GET, "favicon.ico", "favicon");
    infoHandler.registerRoutine(HttpMethod.GET, "alive", "isAlive");
    infoHandler.registerRoutine(HttpMethod.GET, "time", "time");
  }

  /**
   * Creates the protocol-resource which deals with everything about protocols (show protocol, show
   * protocols).
   */
  private void createProtocolResources() {
    ResourceHandler protocolsHandler =
        dispatcher.registerResource("/protocols/", ProtocolResource.class);
    protocolsHandler.registerRoutine(HttpMethod.GET, "", "all");

    ResourceHandler protocolHandler =
        dispatcher.registerResource("/protocols/XY/", new String[] {"XY"},
            new Class<?>[] {Integer.class}, ProtocolResource.class);
    protocolHandler.registerRoutine(HttpMethod.GET, "", "single");
  }

  /**
   * Creates the push-resources handler which deals with push-notifications (subscribe, unsubscribe,
   * notify).
   */
  private void createPushResources() {
    ResourceHandler pushHandler = dispatcher.registerResource("/push/", PushResource.class);
    pushHandler.registerRoutine(HttpMethod.POST, "subscribe", new String[] {"regId"},
        new Class<?>[] {String.class}, "subscribe");
    pushHandler.registerRoutine(HttpMethod.POST, "unsubscribe", new String[] {"regId"},
        new Class<?>[] {String.class}, "unsubscribe");
    pushHandler.registerRoutine(HttpMethod.GET, "notify", new String[] {"message"},
        new Class<?>[] {String.class}, "notify");
    pushHandler.registerRoutine(HttpMethod.GET, "push", new String[] {"type"},
        new Class<?>[] {Integer.class}, "sendPushType");
  }

  /**
   * Creates the recipe-resource which deals with everything about the recipes (e.g. create recipe,
   * show recipe, show recipes)
   */
  private void createRecipeResources() {
    ResourceHandler recipesHandler = dispatcher.registerResource("/recipes/", RecipeResource.class);
    recipesHandler.registerRoutine(HttpMethod.GET, "", "all");
    recipesHandler.registerRoutine(HttpMethod.POST, "create", new String[] {"recipe"},
        new Class<?>[] {String.class}, "createRecipe");

    ResourceHandler recipeHandler =
        dispatcher.registerResource("/recipes/XY/", new String[] {"XY"},
            new Class<?>[] {String.class}, RecipeResource.class);
    recipeHandler.registerRoutine(HttpMethod.GET, "", "single");
  }


  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);

    dispatcher = new Dispatcher(LOGGER, 120);
    /*
     * This is a workaround as the library usually does not take a custom serializer
     */
    Dispatcher.setGsonSerializer(Serializer.getInstance());

    createRecipeResources();
    createProtocolResources();
    createBrewingResources();
    createInfoResources();
    createPushResources();
    createActuatorResources();
  }

  @Override
  protected void service(final HttpServletRequest request, final HttpServletResponse response)
      throws IOException {
    try {
      dispatcher.dispatch(request, response);
    } catch (UnexpectedException error) {
      Throwable cause = error.getCause();
      LOGGER.warn(cause.getClass().getName() + ": " + cause.getMessage());
    }
  }

}
