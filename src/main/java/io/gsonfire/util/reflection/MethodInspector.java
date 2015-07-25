package io.gsonfire.util.reflection;

import java.lang.reflect.Method;

/**
 * Created by julio on 7/25/15.
 */
public class MethodInspector extends AnnotationInspector<Method, Method> {

    @Override
    protected Method[] getDeclaredMembers(Class clazz) {
        return clazz.getDeclaredMethods();
    }

    @Override
    protected Method map(Method member) {
        return member;
    }

}
