package io.gsonfire.postprocessors;

import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExposeMethodResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.gson.FireExclusionStrategy;
import io.gsonfire.gson.FireExclusionStrategyComposite;

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
            for(MappedMethod m: getMappedMethods((Class<T>) src.getClass())){
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

                    ExposeMethodResult exposeMethodResult = m.getAnnotation(ExposeMethodResult.class);

                    m.setAccessible(true);
                    MappedMethod mm = new MappedMethod(m, exposeMethodResult.value(), exposeMethodResult.conflictResolution());
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

        if(clazz == null || clazz == Object.class){
            return allMethods;
        }

        //Add methods declared in the class
        for(Method m: clazz.getDeclaredMethods()){
            allMethods.add(m);
        }

        //Add methods from super class
        allMethods.addAll(getAllMethods(clazz.getSuperclass()));

        //Add methods from interfaces
        for(Class interfaceClass: clazz.getInterfaces()){
            allMethods.addAll(getAllMethods(interfaceClass));
        }

        return allMethods;
    }

}
