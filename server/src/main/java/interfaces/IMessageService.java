/*
 * 
 */
package interfaces;

import messages.Message;
import push.PushMessage;
import push.PushType;


/**
 * Interface for push-communication between server and client.
 *
 * @author Daniel Langerenken
 */
public interface IMessageService {

  /**
   * Notifies subscribed clients with the given message.
   *
   * @param m Message (e.g. object, status, warning)
   */
  void notify(Message m);

  /**
   * Notifies subscribed clients with the given message.
   *
   * @param m Message (e.g. object, status, warning)
   */
  void notify(PushMessage m);

  /**
   * Notifies subscribed clients with the given message and push-type (e.g. ALERT)
   * 
   * @param data message which should be send to the client
   * @param type type of the notification (manual step, ...)
   */
  void notify(String data, PushType type);

  /**
   * Subscribes a client for push-notifications.
   *
   * @param id id of the client (address)
   */
  void subscribe(String id);

  /**
   * unsubscribes a client for push-notifications.
   *
   * @param id id of the client (address)
   */
  void unsubscribe(String id);

  /**
   * Informs all subscribed devices that something went wrong and the brewing process wasnt
   * successfull and could cause damage
   * 
   * @param text message which should be shown to the user
   */
  void alarm(String text);
}
