package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.util.BasicIterable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by julio on 6/23/15.
 */
public final class BasicIterableTypeAdapter extends TypeAdapter<BasicIterable<?>> {

    private final Gson gson;
    private final Type type;

    public BasicIterableTypeAdapter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public void write(JsonWriter out, BasicIterable<?> iterable) throws IOException {
        if(iterable != null) {
            out.beginArray();
            for(Object v: iterable) {
                gson.toJson(v, v.getClass(), out);
            }
            out.endArray();
        } else {
            out.nullValue();
        }
    }

    @Override
    public BasicIterable<?> read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL) {
            return null;
        } else {
            Collection result = new ArrayList();
            in.beginArray();
            while(in.hasNext()) {
                Object obj = gson.fromJson(in, type);
                result.add(obj);
            }
            in.endArray();
            return BasicIterable.of(result);
        }
    }
}
