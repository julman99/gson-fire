package io.gsonfire.gson;

import io.gsonfire.util.reflection.AnnotationInspector;
import io.gsonfire.util.reflection.MethodInspector;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


/**
 * @autor: julio
 */
public class AnnotationInspectorTest {

    @Test
    public void testGetAnnotatedMethods() throws Exception {
        AnnotationInspector inspector = new MethodInspector();

        Collection<Method> methods = inspector.getAnnotatedMembers(A.class, SomeAnnotation.class);

        assertEquals(1, methods.size());
        assertEquals(A.class.getMethod("b"), methods.iterator().next());
    }

    private class A{


        public void a(){

        }

        @SomeAnnotation
        public void b(){

        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SomeAnnotation {

    }
}
