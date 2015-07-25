package io.gsonfire.postprocessors.methodinvoker;

import io.gsonfire.annotations.ExposeMethodResult;
import io.gsonfire.util.reflection.AnnotationInspector;

import java.lang.reflect.Method;

/**
 * Created by julio on 7/25/15.
 */
final class MappedMethodInspector extends AnnotationInspector<Method, MappedMethod> {

    @Override
    protected Method[] getDeclaredMembers(Class clazz) {
        return clazz.getDeclaredMethods();
    }

    @Override
    protected MappedMethod map(Method member) {
        if (member.getParameterTypes().length > 0) {
            throw new IllegalArgumentException("The methods annotated with ExposeMethodResult should have no arguments");
        }

        ExposeMethodResult exposeMethodResult = member.getAnnotation(ExposeMethodResult.class);

        MappedMethod mm = new MappedMethod(member, exposeMethodResult.value(), exposeMethodResult.conflictResolution());
        return mm;
    }
}
