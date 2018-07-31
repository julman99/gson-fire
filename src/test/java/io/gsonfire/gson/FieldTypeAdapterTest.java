package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.FieldTypeAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @autor: julio
 */
public class FieldTypeAdapterTest {

    @Test
    public void testSerialization(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableFieldTypeAdapters();

        Gson gson = builder.createGson();

        Person person = new Person();
        person.id = 1;
        person.name = "john";

        House house = new House();
        house.address = "1 main st";
        house.owner = person;
        house.ownerSecondary = person;

        person.house = house;

        JsonObject tree = gson.toJsonTree(person).getAsJsonObject();
        Assert.assertEquals(person.id, tree.get("id").getAsInt());
        Assert.assertEquals(person.name, tree.get("name").getAsString());
        Assert.assertEquals(person.house.address, tree.get("house").getAsJsonObject().get("address").getAsString());
        Assert.assertEquals(person.house.owner.id, tree.get("house").getAsJsonObject().get("owner").getAsInt());
        Assert.assertEquals(person.house.owner.id, tree.get("house").getAsJsonObject().get("owner2").getAsInt());
    }

    private class Person {

        private int id;

        private String name;

        private House house;
    }

    private class House {

        private String address;

        @FieldTypeAdapter(PersonIdTypeAdapter.class)
        private Person owner;

        @FieldTypeAdapter(PersonIdTypeAdapter.class)
        @SerializedName("owner2")
        private Person ownerSecondary;

    }

    public static class PersonIdTypeAdapter extends TypeAdapter<Person> {

        @Override
        public void write(JsonWriter out, Person value) throws IOException {
            out.value(value.id);
        }

        @Override
        public Person read(JsonReader in) throws IOException {
            return null;
        }
    }

}
