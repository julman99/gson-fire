package io.gsonfire.util.reflection;

import java.lang.reflect.Field;

/**
 * Created by julio on 7/25/15.
 */
public class FieldInspector extends AnnotationInspector<Field, Field> {

    @Override
    protected Field[] getDeclaredMembers(Class clazz) {
        return clazz.getDeclaredFields();
    }

    @Override
    protected Field map(Field member) {
        return member;
    }

}
