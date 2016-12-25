/*
 *
 */
package gson;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

import messages.BrewingAbortedMessage;
import messages.BrewingCompleteMessage;
import messages.BrewingStartMessage;
import messages.ConfirmationMessage;
import messages.ConfirmationRequestMessage;
import messages.EndMessage;
import messages.HopAdditionMessage;
import messages.IodineTestMessage;
import messages.MaltAdditionMessage;
import messages.ManualStepMessage;
import messages.MashingMessage;
import messages.Message;
import messages.PreNotificationMessage;
import messages.StartMessage;
import messages.TemperatureLevelMessage;
import messages.TemperatureMessage;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


/**
 * Json Adapter for messages.
 */
public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {

  /** List of all possible push-messages. */
  private static Map<String, Class<? extends Message>> map =
      new TreeMap<String, Class<? extends Message>>();

  /**
   * TreeMap of all Messages with Class-Names
   */
  static {
    map.put("BrewingAbortedMessage", BrewingAbortedMessage.class);
    map.put("BrewingCompleteMessage", BrewingCompleteMessage.class);
    map.put("BrewingStartMessage", BrewingStartMessage.class);
    map.put("ConfirmationMessage", ConfirmationMessage.class);
    map.put("ConfirmationRequestMessage", ConfirmationRequestMessage.class);
    map.put("HopAdditionMessage", HopAdditionMessage.class);
    map.put("MaltAdditionMessage", MaltAdditionMessage.class);
    map.put("IodineTestMessage", IodineTestMessage.class);
    map.put("ManualStepMessage", ManualStepMessage.class);
    map.put("MashingMessage", MashingMessage.class);
    map.put("Message", Message.class);
    map.put("PreNotificationMessage", PreNotificationMessage.class);
    map.put("TemperatureLevelMessage", TemperatureLevelMessage.class);
    map.put("TemperatureMessage", TemperatureMessage.class);
    map.put("StartMessage", StartMessage.class);
    map.put("EndMessage", EndMessage.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
   * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
   */
  @Override
  public Message deserialize(final JsonElement json, final Type typeOfT,
      final JsonDeserializationContext context) throws JsonParseException {
    Message mData = deserializeMessage(json, "type", "data");
    if (mData instanceof PreNotificationMessage) {
      Message innerMessage = deserializeMessage(json, "innerType", "innerData");
      PreNotificationMessage preNotificationMessage = (PreNotificationMessage) mData;
      preNotificationMessage.setContent(innerMessage);
      return preNotificationMessage;
    }
    return mData;
  }

  private Message deserializeMessage(final JsonElement json, final String typeString,
      final String dataString) {
    Gson gson = Serializer.getBrewingStateAdapterGson();
    JsonElement typeElement = json.getAsJsonObject().get(typeString);
    JsonElement dataElement = json.getAsJsonObject().get(dataString);
    if (typeElement == null || dataElement == null) {
      return null;
    }
    String type = typeElement.getAsString();
    String data = dataElement.getAsString();
    Class<?> clazz = map.get(type);
    if (clazz == null) {
      throw new JsonParseException(String.format("Class for type: %s is not registered", type));
    }
    Object mData = gson.fromJson(data, clazz);
    if (mData instanceof Message) {
      return (Message) mData;
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
   * com.google.gson.JsonSerializationContext)
   */
  @Override
  public JsonElement serialize(final Message src, final Type typeOfSrc,
      final JsonSerializationContext context) {
    Gson gson = Serializer.getBrewingStateAdapterGson();
    JsonObject elem = new JsonObject();
    elem.addProperty("type", src != null ? src.getClass().getSimpleName() : null);
    if (src != null) {
      if (src instanceof PreNotificationMessage) {
        PreNotificationMessage preNotificationMessage = (PreNotificationMessage) src;
        if (preNotificationMessage.getContent() != null) {
          elem.addProperty("innerType", preNotificationMessage.getContent().getClass()
              .getSimpleName());
          elem.addProperty("innerData", gson.toJson(preNotificationMessage.getContent()));
        }
      }
    }
    elem.addProperty("data", src != null ? gson.toJson(src) : null);
    return elem;
  }
}
