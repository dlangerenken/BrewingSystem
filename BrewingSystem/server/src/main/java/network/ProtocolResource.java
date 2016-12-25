/*
 *
 */
package network;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import dispatcher.Context;
import dispatcher.HttpError;
import exceptions.ProtocolNotFoundException;
import exceptions.ProtocolParsingException;
import general.LogSummary;
import general.Protocol;


/**
 * This Resource deals with the Protocol-Management (Show Protocol, Show Protocols, ...)
 *
 * @author Daniel Langerenken
 *
 */
public class ProtocolResource extends RestResource<LogSummary, Protocol, Integer> {

  /**
   * Creates the protocol resource.
   *
   * @param context Context of the servlet
   */
  public ProtocolResource(final Context context) {
    super(context);
  }

  /**
   * Creates the protocol resource.
   *
   * @param context Context of the servlet
   * @param id Id of the single protocol
   */
  public ProtocolResource(final Context context, final Integer id) {
    super(context, id);
  }

  @Override
  public JsonResult<List<LogSummary>> all() {
    Type listType = new TypeToken<List<LogSummary>>() {
      /* Needs to be an empty block */
    }.getType();

    return new JsonResult<List<LogSummary>>(getUserFacade().getProtocolIndex(), listType);
  }

  @Override
  public JsonResult<Protocol> handleSingle() throws HttpError {
    try {
      return new JsonResult<Protocol>(getUserFacade().getProtocolContent(getId()), Protocol.class);
    } catch (ProtocolParsingException e) {
      returnServerException(e);
    } catch (ProtocolNotFoundException e) {
      returnBadRequestException(e);
    }
    return null;
  }

}
