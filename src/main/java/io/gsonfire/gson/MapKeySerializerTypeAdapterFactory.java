package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.StringSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
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
            return (TypeAdapter<T>) new MapKeySerializer(serializerMap, gson, this, typeToken);
        }
    }

    private static class MapKeySerializer extends TypeAdapter<Map> {

        private final Map<Class, StringSerializer> serializerMap;
        private final Gson gson;
        private final MapKeySerializerTypeAdapterFactory parentFactory;
        private final TypeToken typeToken;

        public MapKeySerializer(Map<Class, StringSerializer> serializerMap, Gson gson, MapKeySerializerTypeAdapterFactory parentFactory, TypeToken typeToken) {
            this.gson = gson;
            this.serializerMap = serializerMap;
            this.parentFactory = parentFactory;
            this.typeToken = typeToken;
        }

        @Override
        public void write(JsonWriter jsonWriter, Map map) throws IOException {
            //Create a new map with String keys
            Map<String, Object> stringKeyMap = new LinkedHashMap<String, Object>(map.size());
            for (Map.Entry entry : (Set<Map.Entry>)map.entrySet()) {
                StringSerializer serializer = NO_OP;
                if(entry.getKey() != null) {
                    serializer = findSerializer(entry.getKey().getClass());
                }
                stringKeyMap.put(serializer.toString(entry.getKey()), entry.getValue());
            }

            TypeAdapter delegateTypeAdapter = gson.getDelegateAdapter(this.parentFactory, TypeToken.get(map.getClass()));
            delegateTypeAdapter.write(jsonWriter, stringKeyMap);
        }

        @Override
        public Map read(JsonReader jsonReader) throws IOException {
            Type[] types = $Gson$Types.getMapKeyAndValueTypes(this.typeToken.getType(), this.typeToken.getRawType());
            TypeToken intermediateTypeToken = TypeToken.getParameterized(Map.class, String.class, types[1]);
            TypeAdapter<Map> delegateTypeAdapter = gson.getDelegateAdapter(this.parentFactory, intermediateTypeToken);
            Map<String, Object> intermediateMap = delegateTypeAdapter.read(jsonReader);
            Map finalMap = new LinkedHashMap();
            for(Map.Entry<String, Object> entry: intermediateMap.entrySet()) {
                StringSerializer serializer = findSerializer(TypeToken.get(types[0]).getRawType());
                Object deserializedKey = serializer.fromString(entry.getKey());
                finalMap.put(deserializedKey, entry.getValue());
            }
            return finalMap;
        }

        private StringSerializer findSerializer(Class clazz) {
            StringSerializer result = NO_OP;

            //check cache
            result = serializerMap.get(clazz);
            if(result == null) {
                for (Map.Entry<Class, StringSerializer> entry : serializerMap.entrySet()) {
                    if (entry.getKey().isAssignableFrom(clazz)) {
                        result = entry.getValue();
                        break;
                    }
                }
                serializerMap.put(clazz, result);
            }
            return result;
        }

    }

    private static final class NoOpStringSerializer extends StringSerializer {
        @Override
        public String toString(Object o) {
            return o != null ? o.toString() : "null"; //Gson also returns "null" in this scenario
        }

        @Override
        public Object fromString(String s) {
            return s;
        }
    }
}
