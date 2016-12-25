/*
 * 
 */
package network;

import general.ActuatorDetails;
import dispatcher.Context;
import dispatcher.Result;


/**
 * This class provides information about the actuators.
 *
 * @author Daniel Langerenken
 */
public class ActuatorResource extends BaseResource {

  /**
   * Instantiates the actuator-resource.
   *
   * @param context context which is used for the servlet-environment
   */
  public ActuatorResource(final Context context) {
    super(context);
  }

  /**
   * returns actuator details.
   *
   * @return all information about heater, stirrer and temperature controller
   */
  public Result info() {
    return new JsonResult<ActuatorDetails>(getUserFacade().getCurrentActuatorDetails(),
        ActuatorDetails.class);
  }
}
