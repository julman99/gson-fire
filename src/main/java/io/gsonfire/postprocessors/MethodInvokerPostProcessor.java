package io.gsonfire.postprocessors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExposeMethodResult;
import io.gsonfire.gson.FireExclusionStrategy;
import io.gsonfire.gson.FireExclusionStrategyComposite;
import io.gsonfire.util.reflection.AnnotationInspector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    private static class MappedMethodInspector extends AnnotationInspector<Method, MappedMethod> {

        @Override
        protected Method[] getDeclaredMembers(Class clazz) {
            return clazz.getDeclaredMethods();
        }

        @Override
        protected MappedMethod map(Method member) {
            if(member.getParameterTypes().length > 0){
                throw new IllegalArgumentException("The methods annotated with ExposeMethodResult should have no arguments");
            }

            ExposeMethodResult exposeMethodResult = member.getAnnotation(ExposeMethodResult.class);

            MappedMethod mm = new MappedMethod(member, exposeMethodResult.value(), exposeMethodResult.conflictResolution());
            return mm;
        }
    }

}
