/*
 * 
 */
package network;

import gson.Serializer;
import interfaces.IMessageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import network.push.Constants;
import network.push.Message;
import network.push.Message.Builder;
import network.push.MulticastResult;
import network.push.Result;
import network.push.Sender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import push.PushConstants;
import push.PushMessage;
import push.PushType;
import utilities.PropertyUtil;

import com.google.inject.Singleton;


/**
 * This Service gives the possibility to send push messages directly to registered mobile devices It
 * therefore uses GCM (Google Cloud Messaging).
 *
 * @author Daniel Langerenken
 */
@Singleton
public class PushService implements IMessageService {

  /** How many devices should be informed in one sending period. */
  protected static final int MULTICAST_SIZE = 1000;

  /** Static instance of the log4j-logger. */
  public static final Logger LOGGER = LogManager.getLogger();

  /** Sender which is used to send the messages to the clients. */
  private final Sender sender;

  /** Parallel sending of messages is possible due to this thread pool. */
  private static final Executor THREAD_POOL = Executors.newFixedThreadPool(5);

  /** All connected devices - will be reseted after every reconnect of the server. */
  private final Set<String> connectedDevices = new HashSet<String>();

  /**
   * Initiates the PushService and configures the sender.
   */
  public PushService() {
    sender = new Sender(PropertyUtil.getProperty(PropertyUtil.GCM_API_KEY_PROPERTY));
  }

  /**
   * Returns all connected-devices
   * 
   * @return set of connected devices
   */
  public Set<String> getConnectedDevices() {
    return connectedDevices;
  }

  /**
   * Sends messages in an asynchronous way to avoid any lags.
   *
   * @param partialDevices Devices which should receive the message within this "round"
   * @param messageBuilder Message which should be send
   */
  protected void asyncSend(final List<String> partialDevices, final Builder messageBuilder) {
    /*
     * make a copy
     */
    final List<String> devices = new ArrayList<String>(partialDevices);
    THREAD_POOL.execute(new Runnable() {

      @Override
      public void run() {
        MulticastResult multicastResult;
        try {
          multicastResult = sender.send(messageBuilder.build(), devices, 5);
        } catch (IOException e) {
          LOGGER.error("Error posting messages", e);
          return;
        }
        List<Result> results = multicastResult.getResults();
        /*
         * analyze the results
         */
        for (int i = 0; i < devices.size(); i++) {
          String regId = devices.get(i);
          Result result = results.get(i);
          String messageId = result.getMessageId();
          checkMessageId(messageId, regId, result);
        }
      }
    });
  }

  /**
   * Checks the returned message id and reoves the device in case some device already was registered
   * or if the message id was invalid.
   *
   * @param messageId message-id returned from google
   * @param regId registration id of the client
   * @param result result of the "sending"
   */
  protected void checkMessageId(final String messageId, final String regId, final Result result) {
    if (messageId != null) {

      LOGGER.info("Succesfully sent message to device: " + regId + "; messageId = " + messageId);
      String canonicalRegId = result.getCanonicalRegistrationId();
      if (canonicalRegId != null) {
        /*
         * same device has more than on registration id: update it
         */
        LOGGER.info("canonicalRegId " + canonicalRegId);
        if (connectedDevices.remove(regId)) {
          connectedDevices.add(canonicalRegId);
        }
      }
    } else {
      String error = result.getErrorCodeName();
      if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
        /*
         * application has been removed from device - unregister it
         */
        LOGGER.info("Unregistered device: " + regId);
        if (connectedDevices.remove(regId)) {
          LOGGER.info("Removed regId: " + regId);
        }
      } else {
        LOGGER.error("Error sending message to " + regId + ": " + error);
      }
    }
  }


  @Override
  public void alarm(final String text) {
    LOGGER.warn("Alarm is sent out to the subscribed devices!");
    PushMessage message = new PushMessage(PushType.ALARM, text);
    notify(message);
  }

  @Override
  public void notify(final PushMessage pushMessage) {
    String result = pushMessage(connectedDevices, pushMessage);
    LOGGER.info(result);
  }


  @Override
  public void notify(final messages.Message m) {
    PushType type = PushType.MESSAGE;
    String data = Serializer.getInstance().toJson(m, messages.Message.class);
    PushMessage pushMessage = new PushMessage(type, data);
    String result = pushMessage(connectedDevices, pushMessage);
    LOGGER.info(result);
  }

  /*
   * (non-Javadoc)
   * 
   * @see interfaces.IMessageService#notify(java.lang.String, se.push.PushType)
   */
  @Override
  public void notify(final String data, final PushType type) {
    PushMessage message = new PushMessage(type, data);
    notify(message);
  }

  /**
   * Sends the message to all devices which are passed into this method.
   *
   * @param devices Devices which should receive the message
   * @param messageBuilder Message which should be send to the clients
   * @return Result of the push (e.g. success, failure for devices XY)
   */
  public String pushMessage(final Set<String> devices, final Message.Builder messageBuilder) {
    /*
     * send a multicast message using JSON must split in chunks of 1000 devices (GCM limit)
     */
    String status;
    int total = devices.size();
    List<String> partialDevices = new ArrayList<String>(total);
    int counter = 0;
    int tasks = 0;
    for (String device : devices) {
      counter++;
      partialDevices.add(device);
      int partialSize = partialDevices.size();
      if (partialSize == MULTICAST_SIZE || counter == total) {
        asyncSend(partialDevices, messageBuilder);
        partialDevices.clear();
        tasks++;
      }
    }
    status = "Asynchronously sending " + tasks + " multicast messages to " + total + " devices";
    return status;
  }

  /**
   * Creates a GCM-Message object from a Push-Message object.
   *
   * @param devices the devices
   * @param message Message which should be send
   * @return Result of the push (e.g. success, failure for devices XY)
   */
  public String pushMessage(final Set<String> devices, final PushMessage message) {
    Message.Builder builder = new Message.Builder();
    builder.addData(PushConstants.ACTION, message.getPushType().toString());
    builder.addData(PushConstants.EXTRA_DATA, message.getData());
    return pushMessage(devices, builder);
  }


  @Override
  public void subscribe(final String identifier) {
    connectedDevices.add(identifier);
    LOGGER.info("Added " + identifier + " to connected devices");
  }


  @Override
  public void unsubscribe(final String identifier) {
    connectedDevices.remove(identifier);
    LOGGER.info("Removed " + identifier + " from connected devices");
  }
}
