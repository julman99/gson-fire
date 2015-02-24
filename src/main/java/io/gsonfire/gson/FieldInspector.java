package io.gsonfire.gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @autor: julio
 */
public class FieldInspector {
    private static final Map<Class, Map<Class<? extends Annotation>, Field[]>> cache = new ConcurrentHashMap<Class, Map<Class<? extends Annotation>, Field[]>>();

    public Field[] getAnnotatedFields(Class clazz, Class<? extends Annotation> annotation){
        Field[] fields = getFromCache(clazz, annotation);
        if(fields != null){
            return fields;
        }

        //We only synchronize on a cache miss, to avoid having
        //to synchronize after the cache has been built
        synchronized (cache){
            fields = getFromCache(clazz, annotation);
            if(fields == null){
                Set<Field> fieldList = new HashSet<Field>();

                for(Field m: clazz.getFields()){
                    if(m.isAnnotationPresent(annotation)){
                        m.setAccessible(true);
                        fieldList.add(m);
                    }
                }

                for(Field m: clazz.getDeclaredFields()){
                    if(m.isAnnotationPresent(annotation)){
                        m.setAccessible(true);
                        fieldList.add(m);
                    }
                }

                if(!cache.containsKey(clazz)){
                    cache.put(clazz, new ConcurrentHashMap<Class<? extends Annotation>, Field[]>());
                }

                fields = new Field[fieldList.size()];
                cache.get(clazz).put(annotation, fieldList.toArray(fields));
            }
        }

        return fields;
    }

    private Field[] getFromCache(Class clazz, Class<? extends Annotation> annotation){
        Map<Class<? extends Annotation>, Field[]> annotationMap = cache.get(clazz);
        if(annotationMap != null){
            Field[] fields = annotationMap.get(annotation);
            if(fields != null){
                return fields;
            }
        }
        return null;
    }
}
