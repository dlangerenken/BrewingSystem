/*
 * 
 */
package network;

import gson.Serializer;

import java.lang.reflect.Type;

import dispatcher.HttpStatus;
import dispatcher.MimeType;
import dispatcher.Result;


/**
 * Generic Result which uses the defined serializer instead of "normal" gson.
 *
 * @author Daniel Langerenken
 * @param <T> Element which should be transformed to json
 */
public class JsonResult<T> implements Result {

  /** Result-String which is returned. */
  private final String jsonString;

  /**
   * The result needs the object and type for gson to serialize.
   *
   * @param currentObject the object which should be serialized
   * @param clazz the type of the object
   */
  public JsonResult(final T currentObject, final Type clazz) {
    jsonString = Serializer.getInstance().toJson(currentObject, clazz);
  }

  
  @Override
  public String getMimeType() {
    return MimeType.JSON_APPLICATION;
  }

  
  @Override
  public String getPayload() {
    return jsonString;
  }

  
  @Override
  public HttpStatus getStatus() {
    return jsonString != null ? HttpStatus.C200_OK : HttpStatus.C400_BAD_REQUEST;
  }

}
