package io.gsonfire.gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class reponsible for returning an array of methods that are annotated with a particular class
 * @autor: julio
 */
public final class MethodInspector {

    private static final Map<Class, Map<Class<? extends Annotation>, Method[]>> cache = new ConcurrentHashMap<Class, Map<Class<? extends Annotation>, Method[]>>();

    public Method[] getAnnotatedMethods(Class clazz, Class<? extends Annotation> annotation){
        Method[] methods = getFromCache(clazz, annotation);
        if(methods != null){
            return methods;
        }

        //We only synchronize on a cache miss, to avoid having
        //to synchronize after the cache has been built
        synchronized (cache){
            methods = getFromCache(clazz, annotation);
            if(methods == null){
                Set<Method> methodList = new HashSet<Method>();

                for(Method m: clazz.getMethods()){
                    if(m.isAnnotationPresent(annotation)){
                        methodList.add(m);
                    }
                }

                for(Method m: clazz.getDeclaredMethods()){
                    if(m.isAnnotationPresent(annotation)){
                        m.setAccessible(true);
                        methodList.add(m);
                    }
                }

                if(!cache.containsKey(clazz)){
                    cache.put(clazz, new ConcurrentHashMap<Class<? extends Annotation>, Method[]>());
                }

                methods = new Method[methodList.size()];
                cache.get(clazz).put(annotation, methodList.toArray(methods));
            }
        }

        return methods;
    }

    private Method[] getFromCache(Class clazz, Class<? extends Annotation> annotation){
        Map<Class<? extends Annotation>, Method[]> annotationMap = cache.get(clazz);
        if(annotationMap != null){
            Method[] methods = annotationMap.get(annotation);
            if(methods != null){
                return methods;
            }
        }
        return null;
    }

}
