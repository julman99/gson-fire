package io.gsonfire.postprocessors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.MergeMap;
import io.gsonfire.util.reflection.FieldInspector;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @autor: julio
 */
@Deprecated
public final class MergeMapPostProcessor implements PostProcessor {

    private final FieldInspector fieldInspector;

    public MergeMapPostProcessor(FieldInspector fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public void postDeserialize(Object result, JsonElement src, Gson gson) {
        //nothing
    }

    @Override
    public void postSerialize(JsonElement result, Object src, Gson gson) {
        if(src == null){
            return;
        }
        for(Field f: fieldInspector.getAnnotatedMembers(src.getClass(), MergeMap.class)){
            try {
                Map map = (Map)f.get(src);
                JsonObject resultJsonObject = result.getAsJsonObject();

                //Walk the map and merge it with the json object
                for (Map.Entry<String, JsonElement> entry: gson.toJsonTree(map).getAsJsonObject().entrySet()){
                    resultJsonObject.add(entry.getKey(), entry.getValue());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
