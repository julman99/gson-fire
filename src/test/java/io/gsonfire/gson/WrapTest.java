package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.util.Mapper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by asanchez on 19/02/16.
 */
public class WrapTest {

    @Test
    public void testWrap() {
        Gson gson = new GsonFireBuilder()
                .wrap(A.class, new Mapper<A, String>() {
                    @Override
                    public String map(A from) {
                        return "aWrap";
                    }
                })
                .wrap(B.class, new Mapper<B, String>() {
                    @Override
                    public String map(B from) {
                        return "bWrap";
                    }
                })
                .wrap(D.class, "dWrap")
                .createGson();

        // Wrap A class
        A a = new A();
        a.str1 = "str1";

        JsonObject obj1 = gson.toJsonTree(a).getAsJsonObject();

        assertNotNull(obj1.get("aWrap"));
        assertEquals("str1", obj1.get("aWrap").getAsJsonObject().get("str1").getAsString());

        // Wrap B class
        B b = new B();
        b.str2 = "str2";
        b.a = a;

        JsonObject obj2 = gson.toJsonTree(b).getAsJsonObject();

        assertNotNull(obj2.get("bWrap"));
        assertEquals("str2", obj2.get("bWrap").getAsJsonObject().get("str2").getAsString());
        assertNotNull(obj2.get("bWrap").getAsJsonObject().get("a"));
        assertNotNull(obj2.get("bWrap").getAsJsonObject().get("a").getAsJsonObject().get("aWrap"));
        assertEquals("str1", obj2.get("bWrap").getAsJsonObject().get("a").getAsJsonObject()
                .get("aWrap").getAsJsonObject().get("str1").getAsString());

        // Wrap C class (it is not wrapped)
        C c = new C();
        c.str1 = "str1";
        c.str2 = "str2";

        JsonObject obj3 = gson.toJsonTree(c).getAsJsonObject();

        assertEquals("str1", obj3.get("str1").getAsString());
        assertEquals("str2", obj3.get("str2").getAsString());

        // Wrap D class
        D d = new D();
        d.str1 = "str1";

        JsonObject obj4 = gson.toJsonTree(d).getAsJsonObject();

        assertNotNull(obj4.get("dWrap"));
        assertEquals("str1", obj4.get("dWrap").getAsJsonObject().get("str1").getAsString());
    }

    @Test
    public void testUnwrap() {
        Gson gson = new GsonFireBuilder()
                .wrap(A.class, new Mapper<A, String>() {
                    @Override
                    public String map(A from) {
                        return "aWrap";
                    }
                })
                .wrap(B.class, new Mapper<B, String>() {
                    @Override
                    public String map(B from) {
                        return "bWrap";
                    }
                })
                .wrap(D.class, "dWrap")
                .createGson();

        // Unwrap A class
        JsonParser jsonParser = new JsonParser();
        final JsonObject aJson = jsonParser.parse("{\n" +
                "    aWrap: {\n" +
                "      str1: \"v1\"\n" +
                "    }\n" +
                "}").getAsJsonObject();

        final A a = gson.fromJson(aJson, A.class);
        assertNotNull(a);
        assertEquals("v1", a.str1);

        // Unwrap B class
        final JsonObject bJson = jsonParser.parse("{\n" +
                "  bWrap: {\n" +
                "    a: {\n" +
                "      aWrap: {\n" +
                "        str1: \"v1\"\n" +
                "      }\n" +
                "    },\n" +
                "    str2: \"v2\"\n" +
                "  }\n" +
                "}").getAsJsonObject();

        final B b = gson.fromJson(bJson, B.class);
        assertNotNull(b);
        assertEquals("v2", b.str2);
        assertEquals("v1", b.a.str1);

        // Unwrap C class (it is not unwrapped)
        final JsonObject cJson = jsonParser.parse("{\n" +
                "  str1: \"v1\",\n" +
                "  str2: \"v2\"\n" +
                "}").getAsJsonObject();

        final C c = gson.fromJson(cJson, C.class);
        assertNotNull(c);
        assertEquals("v1", c.str1);
        assertEquals("v2", c.str2);

        // Unwrap D class
        final JsonObject dJson = jsonParser.parse("{\n" +
                "  dWrap: {\n" +
                "    str1: \"v1\"\n" +
                "  }\n" +
                "}").getAsJsonObject();

        final D d = gson.fromJson(dJson, D.class);
        assertNotNull(d);
        assertEquals("v1", d.str1);
    }

    private class A {
        @Expose
        public String str1;
    }

    private class B {
        @Expose
        public String str2;

        @Expose
        public A a;
    }

    private class C {
        @Expose
        public String str1;

        @Expose
        public String str2;
    }

    private class D {
        @Expose
        public String str1;
    }
}
