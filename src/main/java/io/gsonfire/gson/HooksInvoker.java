package io.gsonfire.gson;

import io.gsonfire.annotations.PostDeserialize;
import io.gsonfire.annotations.PreSerialize;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @autor: julio
 */
public final class HooksInvoker {

    private MethodInspector inspector = new MethodInspector();

    public HooksInvoker(){

    }

    public void preSerialize(Object obj){
        invokeAll(obj, PreSerialize.class);
    }

    public void postDeserialize(Object obj){
        invokeAll(obj, PostDeserialize.class);
    }

    private void invokeAll(Object obj, Class<? extends Annotation> annotation){
        for(Method m: inspector.getAnnotatedMethods(obj.getClass(), annotation)){
            try {
                m.invoke(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
