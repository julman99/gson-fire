package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.TypeSelector;
import junit.framework.Assert;
import org.junit.Test;

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

        Base base =  new Base();
        base.kind = "im base";

        String jsona = gson.toJson(a);
        String jsonb = gson.toJson(b);
        String jsonbase = gson.toJson(base);

        Base ba = gson.fromJson(jsona, Base.class);
        Base bb = gson.fromJson(jsonb, Base.class);
        Base bbase = gson.fromJson(jsonbase, Base.class);

        Assert.assertTrue(ba instanceof A);
        Assert.assertTrue(bb instanceof B);
        Assert.assertTrue(bbase.getClass() == Base.class);
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
}
