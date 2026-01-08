package io.gsonfire.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for invoking methods with automatic parameter injection based on type.
 * Used internally for invoking hook methods that may accept optional parameters.
 *
 * @author julio
 */
public class MethodInvoker {

    private final Method method;
    private final List<Class> argsOrder;

    /**
     * Creates a new MethodInvoker for the given method.
     * @param method The method to invoke
     * @param supportedInjectionTypes The set of types that can be automatically injected as parameters
     * @throws IllegalArgumentException if the method has parameters of unsupported types
     */
    public MethodInvoker(Method method, Set<Class> supportedInjectionTypes) {
        this.method = method;
        this.argsOrder = new ArrayList<Class>(supportedInjectionTypes.size());

        for (Class parameterType : this.method.getParameterTypes()) {
            if (supportedInjectionTypes.contains(parameterType)) {
                argsOrder.add(parameterType);
            } else {
                throw new IllegalArgumentException("Cannot auto inject type: " + parameterType);
            }
        }
    }

    /**
     * Invokes the method on the given object, using the supplier to provide parameter values.
     * @param obj The object on which to invoke the method
     * @param supplier The supplier that provides values for injectable parameter types
     * @throws InvocationTargetException if the method throws an exception
     * @throws IllegalAccessException if the method is not accessible
     */
    public void invoke(Object obj, ValueSupplier supplier) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[method.getParameterTypes().length];
        for (int i = 0; i < args.length; i++) {
            args[i] = supplier.getValueForType(argsOrder.get(i));
        }
        this.method.invoke(obj, args);
    }

    /**
     * Supplies values for method parameters based on their type.
     */
    public interface ValueSupplier {

        /**
         * Returns a value to inject for the given parameter type.
         * @param type The parameter type
         * @return The value to inject, or null if not available
         */
        Object getValueForType(Class type);

    }

}
