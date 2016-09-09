package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.TypeSelector;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class TypeSelectorTest {

    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
            .registerTypeSelector(Base.class, new TypeSelector<Base>() {
                @Override
                public Class<? extends Base> getClassForElement(JsonElement readElement) {
                    String kind = readElement.getAsJsonObject().get("kind").getAsString();
                    if(kind.equals("a")){
                        return A.class;
                    } else if(kind.equals("b")) {
                        return B.class;
                    } else if(kind.equals("aa")) {
                        return AA.class;
                    } else {
                        return null;
                    }
                }
            });

        Gson gson = builder.createGson();

        A a = new A();
        a.kind = "a";
        a.a = "im a";

        B b = new B();
        b.kind = "b";
        b.b = "im b";

        AA aa = new AA();
        aa.kind = "aa";
        aa.aa = "im aa";
        aa.a ="im a(subclass)";

        C c = new C();
        c.a = aa;

        Base base =  new Base();
        base.kind = "im base";

        String jsona = gson.toJson(a);
        String jsonb = gson.toJson(b);
        String jsonc = gson.toJson(c);
        String jsonbase = gson.toJson(base);

        Base ba = gson.fromJson(jsona, Base.class);
        Base bb = gson.fromJson(jsonb, Base.class);
        C cc = gson.fromJson(jsonc, C.class);
        Base bbase = gson.fromJson(jsonbase, Base.class);

        Assert.assertTrue(ba instanceof A);
        Assert.assertEquals("im a", ((A)ba).a);

        Assert.assertTrue(bb instanceof B);
        Assert.assertEquals("im b", ((B)bb).b);

        Assert.assertTrue(cc.a instanceof A);
        Assert.assertEquals("im aa", ((AA)c.a).aa);
        Assert.assertEquals("im a(subclass)", ((AA)c.a).a);

        Assert.assertTrue(bbase.getClass() == Base.class);
    }

    @Test
    public void testNull(){
        GsonFireBuilder builder = new GsonFireBuilder()
            .registerTypeSelector(Base.class, new TypeSelector<Base>() {
                @Override
                public Class<? extends Base> getClassForElement(JsonElement readElement) {
                    return AA.class;
                }
            });

        Gson gson = builder.createGson();

        JsonElement json = gson.toJsonTree(null, AA.class);
        assertTrue(json.isJsonNull());
    }

    private class Base{
        public String kind;
    }
    private class A extends Base{
        public String a;
    }
    private class B extends Base{
        public String b;
    }
    private class AA extends A {
        public String aa;
    }
    private class C {
        public A a;
    }
}
