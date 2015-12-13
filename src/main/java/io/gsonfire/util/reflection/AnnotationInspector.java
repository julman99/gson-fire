package io.gsonfire.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class responsible for returning a set of members that are annotated with a particular {@link Annotation} class.
 * The returned members can be mapped to a different type.
 *
 * The result after mapping will be cached for performance reasons
 * @autor: julio
 */
public abstract class AnnotationInspector<T extends AccessibleObject, M> {

    private final ConcurrentMap<Class, ConcurrentMap<Class<? extends Annotation>, Collection<M>>> cache = new ConcurrentHashMap<Class, ConcurrentMap<Class<? extends Annotation>, Collection<M>>>();

    public Collection<M> getAnnotatedMembers(Class clazz, Class<? extends Annotation> annotation){
        if(clazz != null) {
            Collection<M> members = getFromCache(clazz, annotation);
            if (members != null) {
                return members;
            }

            //Cache miss, we need to use reflections to get the fields
            members = getFromCache(clazz, annotation);
            if (members == null) {

                Set<M> memberList = new LinkedHashSet<M>();
                //Add methods declared in the class
                for (T m : getDeclaredMembers(clazz)) {
                    if (m.isAnnotationPresent(annotation)) {
                        m.setAccessible(true);
                        memberList.add(map(m));
                    }
                }

                //Add methods from super class
                memberList.addAll(getAnnotatedMembers(clazz.getSuperclass(), annotation));

                //Add methods from interfaces
                for (Class interfaceClass : clazz.getInterfaces()) {
                    memberList.addAll(getAnnotatedMembers(interfaceClass, annotation));
                }

                ConcurrentMap<Class<? extends Annotation>, Collection<M>> newAnnotationMap = new ConcurrentHashMap<Class<? extends Annotation>, Collection<M>>();
                ConcurrentMap<Class<? extends Annotation>, Collection<M>> storedAnnotationMap = cache.putIfAbsent(clazz, newAnnotationMap);
                storedAnnotationMap = storedAnnotationMap == null ? newAnnotationMap : storedAnnotationMap;
                storedAnnotationMap.put(annotation, memberList);
                return memberList;
            }
        }


        return Collections.emptyList();
    }

    protected abstract T[] getDeclaredMembers(Class clazz);
    protected abstract M map(T member);

    private Collection<M> getFromCache(Class clazz, Class<? extends Annotation> annotation) {
        Map<Class<? extends Annotation>, Collection<M>> annotationMap = cache.get(clazz);
        if(annotationMap != null){
            Collection<M> methods = annotationMap.get(annotation);
            if(methods != null){
                return methods;
            }
        }
        return null;
    }

}
