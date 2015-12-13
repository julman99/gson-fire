package io.gsonfire.util.reflection;

import java.lang.reflect.Method;

/**
 * Created by julio on 7/25/15.
 */
public class MethodInspector extends AbstractMethodInspector<Method> {

    @Override
    protected Method map(Method member) {
        return member;
    }

}
