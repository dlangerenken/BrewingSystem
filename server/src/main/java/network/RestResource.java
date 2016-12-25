/*
 * 
 */
package network;

import java.util.List;

import dispatcher.Context;
import dispatcher.HttpError;
import dispatcher.Result;
import exceptions.NotFoundException;


/**
 * Base class for dealing with rest-resourcen.
 *
 * @author Daniel Langerenken
 * @param <Multiple> Class which should be returned for receiving many objects (Most likely smaller
 *        objects with only basic information)
 * @param <Single> Class which should be returned when only one single object is needed. Contains
 *        all information
 * @param <Id> Id of a single class (if required), most likely String / Integer
 */
public abstract class RestResource<Multiple, Single, Id> extends BaseResource {

  /** Id of the resource. */
  private Id id;

  /**
   * Creates the Resource.
   *
   * @param context Context of the servlet
   */
  public RestResource(final Context context) {
    super(context);
  }

  /**
   * Creates the Resource.
   *
   * @param context Context of the servlet
   * @param id Id of the item
   */
  public RestResource(final Context context, final Id id) {
    super(context);
    this.id = id;
  }

  /**
   * Basic method to show every item of the resource.
   *
   * @return List of <T> objects
   * @throws HttpError if any internal error happened
   */
  public abstract JsonResult<List<Multiple>> all() throws HttpError;

  /**
   * Id of the single item which was passed into the constructor.
   *
   * @return Id of the resource
   */
  public Id getId() {
    return id;
  }

  /**
   * Returns an object of the Parameter V which was genericly added when this resource was
   * initiated.
   * 
   * @param <Single> the single instance of this rest-resource
   *
   * @return V - single item of the resource
   * @throws NotFoundException id was not valid for this item (could't be found)
   * @throws HttpError if any internal server was done
   */
  public abstract JsonResult<Single> handleSingle() throws NotFoundException, HttpError;;

  /**
   * Basic method to show the single item of the resource.
   *
   * @throws HttpError the http error
   */
  public Result single() throws HttpError {
    try {
      return handleSingle();
    } catch (NotFoundException e) {
      returnNotFoundException(e);
    }
    return null;
  }

}
