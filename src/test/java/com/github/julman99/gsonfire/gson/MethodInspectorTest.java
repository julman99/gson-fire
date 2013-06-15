package com.github.julman99.gsonfire.gson;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;


/**
 * @autor: julio
 */
public class MethodInspectorTest {

    @Test
    public void testGetAnnotatedMethods() throws Exception {
        MethodInspector inspector = new MethodInspector();

        Method[] methods = inspector.getAnnotatedMethods(A.class, Deprecated.class);

        assertEquals(1, methods.length);
        assertEquals(A.class.getMethod("b"), methods[0]);
    }

    private class A{


        public void a(){

        }

        @Deprecated
        public void b(){

        }

    }
}
