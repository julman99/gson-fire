package io.gsonfire.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.gsonfire.util.JsonUtils;

/**
 * Created by julio on 8/18/16.
 */
public final class JsonArrayBuilder implements JsonElementBuilder<JsonArray> {

    private final JsonArray array = new JsonArray();

    public JsonArrayBuilder() {

    }

    public JsonArrayBuilder add(JsonElement element) {
        array.add(element);
        return this;
    }

    public JsonArrayBuilder add(JsonElementBuilder builder) {
        array.add(builder.build());
        return this;
    }

    public JsonArrayBuilder add(Boolean bool) {
        array.add(bool);
        return this;
    }

    public JsonArrayBuilder add(Character character) {
        array.add(character);
        return this;
    }

    public JsonArrayBuilder add(Number number) {
        array.add(number);
        return this;
    }

    public JsonArrayBuilder add(String string) {
        array.add(string);
        return this;
    }

    public JsonArrayBuilder addAll(JsonArray jsonArray) {
        array.addAll(jsonArray);
        return this;
    }

    @Override
    public JsonArray build() {
        return JsonUtils.deepCopy(array).getAsJsonArray();
    }

    public static JsonArrayBuilder start() {
        return new JsonArrayBuilder();
    }

}
