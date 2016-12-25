/*
 * 
 */
package network;

import gson.Serializer;
import messages.Message;
import push.PushType;
import utilities.DummyBuilder;
import dispatcher.Context;


/**
 * This resource deals with the push-service and offers subscribe/unsubscribe methods as well as a
 * notify-testing message.
 *
 * @author Daniel Langerenken
 */
public class PushResource extends BaseResource {

  /**
   * Initiates the PushResource.
   *
   * @param context Context of the Servlet
   */
  public PushResource(final Context context) {
    super(context);
  }

  /**
   * A test-notify message method which sends out the message to every subscribed client.
   *
   * @param messageText text which should be send to every client
   * @return a value which states that the method was successfully called
   */
  public String notify(final String messageText) {
    Message message = DummyBuilder.getMessage();
    message.setMessage(messageText);
    getUserFacade().notify(message);
    return "notified";
  }

  /**
   * This is a dummy method to return a valid push-message with a different type.
   *
   * @param mId id of the push-type
   * @return always success
   */
  public String sendPushType(final Integer mId) {
    PushType type = PushType.INFO;
    if (mId < PushType.values().length) {
      type = PushType.values()[mId];
    }
    String data = "This is a test";
    Message m = DummyBuilder.getMessage(7);
    if (type == PushType.MESSAGE) {
      data = Serializer.getInstance().toJson(m, Message.class);
    }
    getUserFacade().notify(data, type);
    return "notified";
  }

  /**
   * Gives the possibility to subscribe to the push-service to receive push messages.
   *
   * @param identifier of the client which is used for responses
   * @return a value which states that the method was successfully called
   */
  public String subscribe(final String identifier) {
    getUserFacade().subscribe(identifier);
    return "subscribed";
  }

  /**
   * Gives the possibility to unsubscribe from the push-service to no longer receive push messages.
   *
   * @param identifier of the client which was used for responses
   * @return a value which states that the method was successfully called
   */
  public String unsubscribe(final String identifier) {
    getUserFacade().unsubscribe(identifier);
    return "unsubscribed";
  }
}
