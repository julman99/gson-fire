package io.gsonfire.util.reflection;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by julio on 10/1/16.
 */
public class CachedReflectionFactoryTest {
    @Test
    public void get() throws Exception {
        CachedReflectionFactory factory = new CachedReflectionFactory();
        Object obj1 = factory.get(Object.class);
        Object obj2 = factory.get(Object.class);
        Object obj3 = factory.get(HashMap.class);

        //Test cache
        assertTrue(obj1 == obj2);
        assertTrue(obj1 != obj3);

        //Test type of objects
        assertTrue(obj1.getClass() == Object.class);
        assertTrue(obj3.getClass() == HashMap.class);
    }


}