package io.gsonfire.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by julio on 8/18/16.
 */
public class JsonUtils {

    private JsonUtils() {

    }

    /**
     * Copies all the property values from the supplied {@link JsonElement}. This method is similar to
     * {@link JsonElement#deepCopy()}. We are not using {@link JsonElement#deepCopy()} because it is not public
     * @param from
     * @return
     */
    public static JsonElement deepCopy(JsonElement from) {
        if(from.isJsonObject()) {
            JsonObject result = new JsonObject();
            for(Map.Entry<String, JsonElement> entry: from.getAsJsonObject().entrySet()) {
                result.add(entry.getKey(), deepCopy(entry.getValue()));
            }
            return result;
        } else if(from.isJsonArray()) {
            JsonArray result = new JsonArray();
            for(JsonElement element: from.getAsJsonArray()) {
                result.add(element);
            }
            return result;
        } else if(from.isJsonPrimitive()) {
            return from;
        } else if(from.isJsonNull()) {
            return from;
        } else {
            return JsonNull.INSTANCE;
        }
    }

}
