package io.gsonfire.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
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
     * @param from from
     * @return JsonElement
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

    public static JsonElement toJsonTree(TypeAdapter typeAdapter, final JsonWriter optionsFrom, Object value) throws IOException {
        return new ConfigurableTypeAdapter<Object>(
                typeAdapter,
                null,
                new Configurable<JsonWriter>() {
                    @Override
                    public void configure(JsonWriter jsonWriter) {
                        jsonWriter.setLenient(optionsFrom.isLenient());
                        jsonWriter.setHtmlSafe(optionsFrom.isHtmlSafe());
                        jsonWriter.setSerializeNulls(optionsFrom.getSerializeNulls());
                    }
                }
        ).toJsonTree(value);
    }

    public static <T> T fromJsonTree(TypeAdapter<T> typeAdapter, final JsonReader originalReader, JsonElement element) throws IOException {
        return new ConfigurableTypeAdapter<T>(
                typeAdapter,
                new Configurable<JsonReader>() {
                    @Override
                    public void configure(JsonReader jsonReader) {
                        jsonReader.setLenient(originalReader.isLenient());
                    }
                },
                null
        ).fromJsonTree(element);
    }

    private static class ConfigurableTypeAdapter<T> extends TypeAdapter<T> {
        private final TypeAdapter<T> originalTypeAdapter;
        private final Configurable<JsonReader> jsonReaderConfigurable;
        private final Configurable<JsonWriter> jsonWriterConfigurable;

        public ConfigurableTypeAdapter(TypeAdapter<T> originalTypeAdapter, Configurable<JsonReader> jsonReaderConfigurable, Configurable<JsonWriter> jsonWriterConfigurable) {
            this.originalTypeAdapter= originalTypeAdapter;
            this.jsonReaderConfigurable = jsonReaderConfigurable;
            this.jsonWriterConfigurable = jsonWriterConfigurable;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (jsonWriterConfigurable != null) {
                jsonWriterConfigurable.configure(out);
            }
            this.originalTypeAdapter.write(out, value);
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (jsonReaderConfigurable != null) {
                jsonReaderConfigurable.configure(in);
            }
            return this.originalTypeAdapter.read(in);
        }
    }

    private interface Configurable<T> {
        void configure(T configurable);
    }

}
