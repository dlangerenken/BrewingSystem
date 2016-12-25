/*
 * 
 */
package network;

import impl.Application;
import interfaces.IUserFacadeService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import dispatcher.Context;
import dispatcher.HttpError;
import dispatcher.HttpStatus;
import dispatcher.Resource;


/**
 * This BaseResource offers the interface to the userFacade.
 *
 * @author Daniel Langerenken
 */
public abstract class BaseResource extends Resource {

  /** UserFacade which should be called if information from the server is required. */
  @Inject
  private IUserFacadeService userFacade;

  /** Log4j-Logger-Instance for logging purposes. */
  public static final Logger LOGGER = LogManager.getLogger();

  /**
   * Creates a BaseResource.
   *
   * @param context Context of the servlet
   */
  public BaseResource(final Context context) {
    super(context);
    /*
     * As Resources are not created by ourselve we need to inject the members via an injector
     * manually
     */
    Application.inject(this);
  }

  /**
   * Returns the UserFacade which is injected automatically.
   *
   * @return UserFacade to communicate with the server
   */
  protected IUserFacadeService getUserFacade() {
    return userFacade;
  }

  /**
   * Returns an exception and 400 Error Code.
   * 
   * @param e exception if available
   *
   * @throws HttpError throws a 400 Error Code
   */
  protected void returnBadRequestException(final Exception e) throws HttpError {
    throw new HttpError(HttpStatus.C400_BAD_REQUEST, e != null ? e.getMessage() : "", e);
  }

  /**
   * Returns an exception and 404 Error Code.
   * 
   * @param e exception if available
   *
   * @throws HttpError throws a 404 Error Code
   */
  protected void returnNotFoundException(final Exception e) throws HttpError {
    throw new HttpError(HttpStatus.C404_NOT_FOUND, e != null ? e.getMessage() : "", e);
  }

  /**
   * Returns an exception and 500 Error Code.
   * 
   * @param e exception if available
   *
   * @throws HttpError throws a 400 Error Code
   */
  protected void returnServerException(final Exception e) throws HttpError {
    throw new HttpError(HttpStatus.C500_INTERNAL_SERVER_ERROR, e != null ? e.getMessage() : "", e);
  }
}
