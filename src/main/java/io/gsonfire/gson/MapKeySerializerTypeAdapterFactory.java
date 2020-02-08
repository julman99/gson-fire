package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.StringSerializer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MapKeySerializerTypeAdapterFactory implements TypeAdapterFactory {

    private static final StringSerializer NO_OP = new NoOpStringSerializer();

    private final Map<Class, StringSerializer> serializerMap;

    public MapKeySerializerTypeAdapterFactory(Map<Class, StringSerializer> serializerMap) {
        this.serializerMap = new LinkedHashMap<Class, StringSerializer>(serializerMap);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if(!Map.class.isAssignableFrom(typeToken.getRawType())) {
            return gson.getDelegateAdapter( this, typeToken);
        } else {
            return (TypeAdapter<T>) new MapKeySerializer(serializerMap, gson, this);
        }
    }

    private static class MapKeySerializer extends TypeAdapter<Map> {

        private final Map<Class, StringSerializer> serializerMap;
        private final Gson gson;
        private final MapKeySerializerTypeAdapterFactory parentFactory;

        public MapKeySerializer(Map<Class, StringSerializer> serializerMap, Gson gson, MapKeySerializerTypeAdapterFactory parentFactory) {
            this.gson = gson;
            this.serializerMap = serializerMap;
            this.parentFactory = parentFactory;
        }

        @Override
        public void write(JsonWriter jsonWriter, Map map) throws IOException {
            //Create a new map with String keys
            Map<String, Object> stringKeyMap = new LinkedHashMap<String, Object>(map.size());
            for (Map.Entry entry : (Set<Map.Entry>)map.entrySet()) {
                 StringSerializer serializer = serializerMap.get(entry.getKey().getClass());
                 if(serializer == null) {
                     //try to find the proper serializer
                     serializer = findSerializer(entry.getKey().getClass());
                 }
                 stringKeyMap.put(serializer.toString(entry.getKey()), entry.getValue());
            }

            TypeAdapter delegateTypeAdapter = gson.getDelegateAdapter(this.parentFactory, TypeToken.get(map.getClass()));
            delegateTypeAdapter.write(jsonWriter, stringKeyMap);
        }

        @Override
        public Map read(JsonReader jsonReader) throws IOException {
            return null;
        }

        private StringSerializer findSerializer(Class clazz) {
            StringSerializer result = NO_OP;
            for(Map.Entry<Class, StringSerializer> entry: serializerMap.entrySet()) {
                if(entry.getKey().isAssignableFrom(clazz)) {
                    result = entry.getValue();
                    break;
                }
            }
            serializerMap.put(clazz, result);
            return result;
        }

    }

    private static final class NoOpStringSerializer extends StringSerializer {
        @Override
        public String toString(Object o) {
            return o.toString();
        }

        @Override
        public Object fromString(String s) {
            return s;
        }
    }
}
