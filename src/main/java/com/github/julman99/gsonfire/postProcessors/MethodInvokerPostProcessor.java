package com.github.julman99.gsonfire.postProcessors;

import com.github.julman99.gsonfire.PostProcessor;
import com.github.julman99.gsonfire.annotations.ExposeMethodResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor: julio
 */
public class MethodInvokerPostProcessor<T> implements PostProcessor<T> {

    private static Map<Class, MappedMethod[]> methodMap = new ConcurrentHashMap<Class, MappedMethod[]>();

    @Override
    public void postDeserialize(T result, JsonElement src, Gson gson) {
        //nothing here
    }

    @Override
    public void postSerialize(JsonElement result, T src, Gson gson) {
        if(result.isJsonObject()){
            JsonObject jsonObject = result.getAsJsonObject();
            for(MappedMethod m: getMappedMethods((Class<T>) src.getClass())){
                Object value = null;
                try {
                    value = m.method.invoke(src);
                    jsonObject.add(m.fieldName, gson.toJsonTree(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized MappedMethod[] getMappedMethods(Class<? super T> clazz){
        MappedMethod[] methods = methodMap.get(clazz);
        if(methods == null){
            List<MappedMethod> methodList = new ArrayList<MappedMethod>();

            //public members
            for(Method m: this.getAllMethods(clazz)){
                if(m.isAnnotationPresent(ExposeMethodResult.class)){

                    if(m.getParameterTypes().length > 0){
                        throw new InvalidParameterException("The methods annotated with ExposeMethodResult should have no arguments");
                    }

                    m.setAccessible(true);
                    MappedMethod mm = new MappedMethod();
                    mm.method = m;
                    mm.fieldName = m.getAnnotation(ExposeMethodResult.class).value();
                    methodList.add(mm);
                }
            }

            methods = new MappedMethod[methodList.size()];
            methodList.toArray(methods);
            methodMap.put(clazz, methods);
        }
        return methods;
    }

    private Collection<Method> getAllMethods(Class clazz){
        HashSet<Method> allMethods = new HashSet<Method>();

        if(clazz == Object.class){
            return allMethods;
        }

        for(Method m: clazz.getDeclaredMethods()){
            allMethods.add(m);
        }
        allMethods.addAll(getAllMethods(clazz.getSuperclass()));
        return allMethods;
    }

    private static class MappedMethod{
        public Method method;
        public String fieldName;
    }
}
