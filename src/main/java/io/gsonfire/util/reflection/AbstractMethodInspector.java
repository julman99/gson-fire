package io.gsonfire.util.reflection;

import java.lang.reflect.Method;

/**
 * Created by julio on 12/12/15.
 */
public abstract class AbstractMethodInspector<M> extends AnnotationInspector<Method, M> {
    @Override
    protected Method[] getDeclaredMembers(Class clazz) {
        return clazz.getDeclaredMethods();
    }
}
