package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.StringSerializer;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapKeyTest {

    @Test
    public void test() {
        Map<Class, StringSerializer> serializerMap = new HashMap<Class, StringSerializer>();
        serializerMap.put(A.class, new StringSerializer<A>() {
            @Override
            public String toString(A from) {
                return ((A)from).key;
            }

            @Override
            public A fromString(String s) {
                return new A(s);
            }
        });

        Map<A, String> map = new HashMap<A, String>();
        Map<Object, String> map2 = new HashMap<Object, String>();

        Gson gson = new GsonFireBuilder()
            .createGsonBuilder()
            .registerTypeAdapterFactory(new MapKeySerializerTypeAdapterFactory(serializerMap))
            .registerTypeAdapter(A.class, new TypeAdapter<A>() {
                @Override
                public void write(JsonWriter jsonWriter, A a) throws IOException {
                    jsonWriter.value(a.key);
                }

                @Override
                public A read(JsonReader jsonReader) throws IOException {
                    return new A(jsonReader.nextString());
                }
            })
            .create();
        
        map.put(new A("a"), "val");
        map2.put(new AA("a"), "val");
        map2.put(new AA("b"), "val");

        String res = gson.toJson(map);
        String res2 = gson.toJson(map2);

    }


    public static class A {
        String key;

        public A(String key) {
            this.key = key;
        }
    }

    public static class AA extends A {
        public AA(String key) {
            super(key);
        }
    }
}
