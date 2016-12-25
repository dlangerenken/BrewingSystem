/*
 *
 */
package gson;

import general.BrewingState;
import general.HopAddition;
import general.IodineTest;
import general.MaltAddition;
import general.TemperatureLevel;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;


/**
 * Json Adapter for BrewingState
 */
public class BrewingStateAdapter implements JsonSerializer<BrewingState>,
    JsonDeserializer<BrewingState> {

  /** List of all possible brewing-state-data-objects */
  private static Map<String, Type> map = new TreeMap<String, Type>();

  /**
   * TreeMap of all possible Objects with Class-Names
   */
  static {
    map.put("HopAddition", HopAddition.class);
    map.put("MaltAddition", MaltAddition.class);
    map.put("TemperatureLevel", TemperatureLevel.class);
    map.put("IodineTest", IodineTest.class);

    Type hopAdditionListType = new TypeToken<List<HopAddition>>() {
      /* Needs to be an empty block */
    }.getType();
    map.put("HopAdditionList", hopAdditionListType);

    Type maltAdditionListType = new TypeToken<List<MaltAddition>>() {
      /* Needs to be an empty block */
    }.getType();
    map.put("MaltAdditionList", maltAdditionListType);

    Type listType = new TypeToken<List<?>>() {
      /* Needs to be an empty block */
    }.getType();
    map.put("List", listType);
  }

  @Override
  public BrewingState deserialize(final JsonElement json, final Type typeOfT,
      final JsonDeserializationContext context) throws JsonParseException {
    Gson gson = new Gson();

    Object mData = null;
    JsonElement typeElement = json.getAsJsonObject().get("type");
    JsonElement dataElement = json.getAsJsonObject().get("data");
    if (typeElement != null && dataElement != null && !typeElement.isJsonNull()
        && !dataElement.isJsonNull()) {
      String type = typeElement.getAsString();
      String data = dataElement.getAsString();
      Type clazz = map.get(type);
      if (clazz != null) {
        mData = gson.fromJson(data, clazz);
      }
    }

    int state = json.getAsJsonObject().get("state").getAsNumber().intValue();
    BrewingState brewingState = BrewingState.fromValue(state);
    brewingState.setData(mData);
    return brewingState;
  }

  @Override
  public JsonElement serialize(final BrewingState src, final Type typeOfSrc,
      final JsonSerializationContext context) {
    Gson gson = new Gson();
    JsonObject elem = new JsonObject();
    elem.addProperty("state",
        src != null ? new JsonPrimitive(BrewingState.toValue(src)).getAsNumber() : null);
    String simpleName =
        src != null && src.getData() != null ? src.getData().getClass().getSimpleName() : null;
    if (src != null && src.getData() != null && src.getData() instanceof List<?>) {
      switch (src.getState()) {
        case MASHING:
          simpleName = "MaltAdditionList";
          break;
        case HOP_COOKING:
          simpleName = "HopAdditionList";
          break;
        default:
          simpleName = "List";
          break;
      }
    }
    elem.addProperty("type", simpleName);
    elem.addProperty("data", src != null && src.getData() != null ? gson.toJson(src.getData())
        : null);
    return elem;
  }
}
