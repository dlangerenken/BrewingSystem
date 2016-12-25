/*
 * 
 */
package network;

import java.util.Date;
import java.util.Properties;

import utilities.PropertyUtil;
import dispatcher.Context;
import dispatcher.Result;


/**
 * This class shows information about the system (e.g. commands, tutorials)
 * 
 * @author Daniel Langerenken
 *
 */
public class InfoResource extends BaseResource {

  /**
   * Creates the Info-Resource.
   *
   * @param context Context of the servlet
   */
  public InfoResource(final Context context) {
    super(context);
  }

  /**
   * returns the current status - if available, it returns true (otherwise this resource is not
   * reachable)
   * 
   * @return true if alive.
   */
  public boolean isAlive() {
    return true;
  }
  

  /**
   * Catches the favicon-request
   * @return nothing
   */
  public String favicon() {
    return "";
  }

  /**
   * The information which should be shown.
   *
   * @return Information about the system
   */
  public Result info() {
    Properties properties = PropertyUtil.getProperties();
    properties.setProperty("ProjectFolder", PropertyUtil.PROJECT_FOLDER_PATH);
    properties.setProperty("RecipePath", PropertyUtil.RECIPE_PATH);

    return new JsonResult<Properties>(properties, Properties.class);
  }

  /**
   * Returns the server time
   * 
   * @return long - milliseconds of current server time
   */
  public long time() {
    return new Date().getTime();
  }

}
