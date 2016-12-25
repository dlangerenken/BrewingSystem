/*
 * 
 */
package network;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import interfaces.INetworkService;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.PropertyUtil;

import com.google.inject.Singleton;

import exceptions.ServerAlreadyRunningException;


/**
 * The network controller deals with the whole network communication and can only interact with the
 * IUserFacade-interface
 * 
 * It redirects requests to the appropriate resources.
 *
 * @author Daniel Langerenken
 */
@Singleton
public class NetworkController implements INetworkService {

  /** The path the server should run on (address/thispath). */
  public static final String MYAPP = "/";

  /** Server which is used for http-communication. */
  private Undertow server;

  /** Logger which is used for logging. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Returns the DispatchServlet which redirects calls to the apropriate resources.
   *
   * @return ServletInfo for the undertow-framework
   */
  private ServletInfo getDispatchServlet() {
    return servlet("DispatchServlet", DispatchServlet.class).addMapping("/*");
  }

  @Override
  public void shutdownServer() {
    server.stop();
  }

  @Override
  public void startServer() throws ServerAlreadyRunningException {
    startServer("0.0.0.0", PropertyUtil.getHttpPort());
  }

  @Override
  public void startServer(final String url, final int port) throws ServerAlreadyRunningException {
    if (server != null) {
      throw new ServerAlreadyRunningException();
    }
    try {
      DeploymentInfo servletBuilder =
          deployment().setClassLoader(NetworkController.class.getClassLoader())
              .setContextPath(MYAPP).setDeploymentName("brewing.war")
              .addServlet(getDispatchServlet());

      DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
      manager.deploy();
      HttpHandler servletHandler = manager.start();
      PathHandler path =
          Handlers.path(Handlers.redirect(MYAPP)).addPrefixPath(MYAPP, servletHandler);
      server = Undertow.builder().addHttpListener(port, url).setHandler(path).build();
      server.start();
      LOGGER.info(String.format("Server started on Address: %s Port: %d", url, port));
    } catch (ServletException e) {
      throw new ServerAlreadyRunningException(e);
    }
  }

}
