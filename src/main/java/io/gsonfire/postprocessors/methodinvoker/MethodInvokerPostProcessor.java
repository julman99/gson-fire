package io.gsonfire.postprocessors.methodinvoker;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExposeMethodResult;
import io.gsonfire.gson.FireExclusionStrategy;
import io.gsonfire.gson.FireExclusionStrategyComposite;

import java.lang.reflect.InvocationTargetException;

/**
 * @autor: julio
 */
public final class MethodInvokerPostProcessor<T> implements PostProcessor<T> {

    private static MappedMethodInspector methodInspector = new MappedMethodInspector();

    private final FireExclusionStrategy serializationExclusionStrategy;

    public MethodInvokerPostProcessor() {
        this(new FireExclusionStrategyComposite());
    }

    public MethodInvokerPostProcessor(FireExclusionStrategy serializationExclusionStrategy) {
        this.serializationExclusionStrategy = serializationExclusionStrategy;
    }

    @Override
    public void postDeserialize(T result, JsonElement src, Gson gson) {
        //nothing here
    }

    @Override
    public void postSerialize(JsonElement result, T src, Gson gson) {
        if(result.isJsonObject()){
            JsonObject jsonObject = result.getAsJsonObject();
            for(MappedMethod m: methodInspector.getAnnotatedMembers(src.getClass(), ExposeMethodResult.class)){
                if(!serializationExclusionStrategy.shouldSkipMethod(m)) {
                    try {
                        if (m.getConflictResolutionStrategy() == ExposeMethodResult.ConflictResolutionStrategy.OVERWRITE || (m.getConflictResolutionStrategy() == ExposeMethodResult.ConflictResolutionStrategy.SKIP && !jsonObject.has(m.getSerializedName()))) {
                            Object value = m.getMethod().invoke(src);
                            jsonObject.add(m.getSerializedName(), gson.toJsonTree(value));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
