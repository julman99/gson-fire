package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.gsonfire.annotations.PostDeserialize;
import io.gsonfire.annotations.PreSerialize;
import io.gsonfire.util.reflection.AbstractMethodInspector;
import io.gsonfire.util.reflection.MethodInvoker;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @autor: julio
 */
public final class HooksInvoker {

    private static final Set<Class> SUPPORTED_TYPES = new HashSet<Class>(Arrays.asList(
        JsonElement.class,
        Gson.class
    ));

    private AbstractMethodInspector<MethodInvoker> inspector = new AbstractMethodInspector<MethodInvoker>() {
        @Override
        protected MethodInvoker map(Method member) {
            return new MethodInvoker(member, SUPPORTED_TYPES) ;
        }
    };

    public HooksInvoker(){

    }

    public void preSerialize(Object obj){
        invokeAll(obj, PreSerialize.class, null, null);
    }

    public void postDeserialize(Object obj, JsonElement jsonElement, Gson gson){
        invokeAll(obj, PostDeserialize.class, jsonElement, gson);
    }

    private void invokeAll(Object obj, Class<? extends Annotation> annotation, JsonElement jsonElement, Gson gson){
        if(obj != null) {
            for (MethodInvoker m : inspector.getAnnotatedMembers(obj.getClass(), annotation)) {
                try {
                    m.invoke(obj, new HooksInvokerValueSupplier(jsonElement, gson));
                } catch (IllegalAccessException e) {
                    throw new HookInvocationException("Exception during hook invocation: " + annotation.getSimpleName(), e);
                } catch (InvocationTargetException e) {
                    throw new HookInvocationException("Exception during hook invocation: " + annotation.getSimpleName(), e.getTargetException());
                }
            }
        }
    }

    private static class HooksInvokerValueSupplier implements MethodInvoker.ValueSupplier {

        private final JsonElement jsonElement;
        private final Gson gson;

        private HooksInvokerValueSupplier(JsonElement jsonElement, Gson gson) {
            this.jsonElement = jsonElement;
            this.gson = gson;
        }

        @Override
        public Object getValueForType(Class type) {
            if(type == JsonElement.class) {
                return jsonElement;
            } else if (type == Gson.class) {
                return gson;
            } else {
                return null;
            }
        }
    }

}
