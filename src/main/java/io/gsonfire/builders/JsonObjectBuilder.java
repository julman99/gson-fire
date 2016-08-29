package io.gsonfire.builders;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.gsonfire.util.JsonUtils;

import java.util.Map;

/**
 * Created by julio on 8/18/16.
 */
public final class JsonObjectBuilder implements JsonElementBuilder<JsonObject> {

    private final JsonObject object = new JsonObject();

    public JsonObjectBuilder() {

    }

    public JsonObjectBuilder set(String property, String value) {
        object.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder set(String property, Number value) {
        object.addProperty(property, value);
        return this;
    }
    public JsonObjectBuilder set(String property, Boolean value) {
        object.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder set(String property, JsonElement value) {
        object.add(property, value);
        return this;
    }

    public JsonObjectBuilder set(String property, JsonElementBuilder builder) {
        object.add(property, builder.build());
        return this;
    }

    public JsonObjectBuilder setNull(String property) {
        object.add(property, JsonNull.INSTANCE);
        return this;
    }

    /**
     * Copies all the property/values from #jsonObject into the json object being built by this builder
     * @param jsonObject
     * @return
     */
    public JsonObjectBuilder merge(JsonObject jsonObject) {
        for(Map.Entry<String, JsonElement> entry: JsonUtils.deepCopy(jsonObject).getAsJsonObject().entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public JsonObject build() {
        return JsonUtils.deepCopy(object).getAsJsonObject();
    }

}
