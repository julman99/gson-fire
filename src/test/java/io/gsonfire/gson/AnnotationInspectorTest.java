package io.gsonfire.gson;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.Test;

import io.gsonfire.annotations.ExposeMethodResult;
import io.gsonfire.util.reflection.AnnotationInspector;
import io.gsonfire.util.reflection.MethodInspector;


/**
 * @autor: julio
 */
public class AnnotationInspectorTest {

    @Test
    public void testGetAnnotatedMethods() throws Exception {
        AnnotationInspector inspector = new MethodInspector();

		Collection<Method> methods = inspector.getAnnotatedMembers(A.class, ExposeMethodResult.class);

        assertEquals(1, methods.size());
        assertEquals(A.class.getMethod("b"), methods.iterator().next());
    }

    private class A{


        public void a(){

        }

		@ExposeMethodResult("b")
        public void b(){

        }

    }
}
