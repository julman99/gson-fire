package io.gsonfire.postprocessors.methodinvoker;

import java.lang.reflect.Method;

import io.gsonfire.annotations.ExposeMethodParam;
import io.gsonfire.util.reflection.AnnotationInspector;

final class MappedMethodParamInspector extends AnnotationInspector<Method, MappedMethod> {

    @Override
    protected Method[] getDeclaredMembers(Class clazz) {
        return clazz.getDeclaredMethods();
    }

    @Override
    protected MappedMethod map(Method member) {
		if (member.getParameterTypes().length != 1) {
			throw new IllegalArgumentException("The methods annotated with ExposeMethodParam should have exaclty one argument");
        }

		ExposeMethodParam exposeMethodParam = member.getAnnotation(ExposeMethodParam.class);

		MappedMethod mm = new MappedMethod(member, exposeMethodParam.value(), exposeMethodParam.conflictResolution());
        return mm;
    }
}
