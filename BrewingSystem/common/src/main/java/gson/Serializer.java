/*
 * 
 */
package gson;

import messages.Message;
import general.BrewingState;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * This Serializer is used for every gson-operation we deal with as it correctly parses messages and
 * other objects via adapters.
 *
 * @author Daniel Langerenken
 */
public class Serializer {

  /**
   * Returns a Gson-Instance which can convert an object to json and back to the object safely
   * Usage: e.g. Message -> getInstance().toJson(MyMessage, Message.class),
   * getInstance().fromJson(messageText, Message.class)
   * 
   * @return Gson-Instance with type-adapters
   */
  public static Gson getInstance() {
    return new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter())
        .registerTypeAdapter(BrewingState.class, new BrewingStateAdapter())
        .enableComplexMapKeySerialization().create();

  }
  
  /**
   * Returns a Gson-Instance which can convert an object to json and back to the object safely
   * Usage: e.g. Message -> getInstance().toJson(MyMessage, Message.class),
   * getInstance().fromJson(messageText, Message.class)
   * 
   * @return Gson-Instance with type-adapters - just the brewing state adapter included
   */
  public static Gson getBrewingStateAdapterGson() {
    return new GsonBuilder().registerTypeAdapter(BrewingState.class, new BrewingStateAdapter())
        .enableComplexMapKeySerialization().create();
  }
}
