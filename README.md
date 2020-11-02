<img src="https://travis-ci.org/julman99/gson-fire.svg?branch=master">

# Gson on Fire

This project implements some useful features missing from the extremely useful [Gson project](https://code.google.com/p/google-gson/)

There is no attempt to modify any existing behavior or existing class of Gson. In fact, this project depends on Gson.
The main objective is to extend Gson using TypeAdapter and TypeAdapterFactory instances to support more features.

## Features

All of the features can be accessed by the ```GsonFireBuilder```. This class will build internally a ```GsonBuilder```
with all the desired features from which a ```Gson``` instance can be obtained.

### Pre Processors

Ability to alter a JsonElement before it is converted into an object.

```java
 GsonFireBuilder builder = new GsonFireBuilder()
    .registerPreProcessor(SomeClass.class, new PreProcessor<SomeClass>() {
        @Override
        public void preDeserialize(Class<? extends A> clazz, JsonElement src, Gson gson) {
            //Here you can add logic to change the src object before it gets converted into the Class clazz
        }
    });
Gson gson = builder.createGson();
```

### Post Processors

Ability to alter a JsonElement after it has been generated from an object.

```java
 GsonFireBuilder builder = new GsonFireBuilder()
    .registerPostProcessor(SomeClass.class, new PostProcessor<SomeClass>() {
        @Override
        public void postDeserialize(SomeClass result, JsonElement src) {
            //Here you can add logic to change the SomeClass instance
        }

        @Override
        public void postSerialize(JsonElement result, SomeClass src) {
            //Here you can inject new fields into the result JsonElement
        }
    });
Gson gson = builder.createGson();
```

### Type Selectors

Useful when you have a base class and some sub-classes and you need to de-serialize some json into one of the sub-classes.
The sub-class type will be determined by a field on the json.

```java
GsonFireBuilder builder = new GsonFireBuilder()
    .registerTypeSelector(Base.class, new TypeSelector<Base>() {
        @Override
        public Class<? extends Base> getClassForElement(JsonElement readElement) {
            String kind = readElement.getAsJsonObject().get("kind").getAsString();
            if(kind.equals("a")){
                return A.class; //This will cause Gson to deserialize the json mapping to A
            } else if(kind.equals("b")) {
                return B.class; //This will cause Gson to deserialize the json mapping to B
            } else {
                return null; //returning null will trigger Gson's default behavior
            }
        }
    });
Gson gson = builder.createGson();
```

### Expose your methods

You can annotate methods to be automatically evaluated and serialized

```java

public void SomeClass{

    @ExposeMethodResult("name")
    public String getName(){
        return "a";
    }

}

//Then
GsonFireBuilder builder = new GsonFireBuilder()
    .enableExposeMethodResult(); //This will make Gson to evaluate and
                                 //serialize all methods annotated with @ExposeMethodResult

```

You can use ```GsonFireBuilder.addSerializationExclusionStrategy``` if you want to add custom exclusion strategies for
some methods.

### Date format

Dates can be serialized either to a unix epoch with or without milliseconds. Also [RFC3339](http://www.ietf.org/rfc/rfc3339.txt) is supported

```java
GsonFireBuilder builder = new GsonFireBuilder();

//then
builder.dateSerializationPolicy(DateSerializationPolicy.unixTimeMillis);
//or
builder.dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds);
//or
builder.dateSerializationPolicy(DateSerializationPolicy.rfc3339); //date and time will be serialized
//or
builder.dateSerializationPolicy(DateSerializationPolicy.rfc3339Date); //only date portion will be serialized

```

If no policy is specified, the default Gson behavior will be used

### Hooks

Some special annotated methods will be invoked in your java object before serializing
and after deserializing

```java

public void SomeClass{

    @PreSerialize
    public void preSerializeLogic(){
        //this method will get invoked just before
        //the class is serialized to gson
    }

    @PostDeserialize
    public void postDeserializeLogic(){
        //this method will get invoked just after
        //the class is instantiated from a json

        //NOTE: this gets invoked before the PostProcessors
    }

}

//Later

Gson builder = new GsonFireBuilder()
    .enableHooks(SomeClass.class);

Gson gson = builder.createGson();
```

Any `Exception` thrown inside the hooks will be wrapped into a `HookInvocationException`

### Iterable Serialization

By default Gson does not serializes ```Iterable``` implementations. Gson on Fire provides a wrapper class called 
```SimpleIterable``` that allows full serialization an deserialization of Iterables.

<b>Serialization</b>
```java
Gson gson = new GsonFireBuilder().createGson();
SimpleIterable<Integer> simpleIterable = SimpleIterable.of(anotherIterable);
String json = gson.toJson(simpleIterable) //this will serialize the contents of "anotherIterable" as a json array

```

<b>De-serialization</b>
```java
Gson gson = new GsonFireBuilder().createGson();
SimpleIterable<Integer> simpleIterable = gson.fromJson("[1,2,3]", new TypeToken<SimpleIterable<Integer>>(){}.getType());

//Now simpleIterable allows to iterate on the three integer values
for(Integer i: simpleIterable) {
    ///...
}

```

### Excude fields depending on its value

Gson allows to define custom exclusion strategies for fields. However it is not possible to exclude a field depending
on the value that it contains. This is the way to do it with Gson on Fire: 


```java
public class SomeClass {

    @ExcludeByValue(ExcludeLogic.class)
    private String someField;

}

public class ExcludeLogic implements ExclusionByValueStrategy<String> {

    @Override
    public boolean shouldSkipField(String fieldValue) {
        //some custom condition
    }

}
```
Then you need to enable exclusion by value on Gson on Fire:
```java
Gson builder = new GsonFireBuilder()
    .enableExclusionByValue();

Gson gson = builder.createGson();

```
### Wrap/unwrap classes
Ability to wrap a class with a string after serialization and unwrap that class before deserialization. This is useful when you write JSON objects and need to specify the name of the class to deserialize.

```java
public class MyClass {
    @Expose
    public String field;
}
```

It is necessary to specify a Mapper or a String:

```java
GsonFireBuilder gsonFireBuilder = new GsonFireBuilder()
    .wrap(MyClass.class, new Mapper<MyClass, String>() {
        @Override
        public String map(MyClass myClass) {
            return "myClass";
        }
    });
```

```java
GsonFireBuilder gsonFireBuilder = new GsonFireBuilder()
    .wrap(MyClass.class, "myClass");
```

The previous code will serialize to / deserialize from:

```
{
  myClass: {
    field: "v1"
  }
}
```

instead of

```
{
  field: "v1"
}
```
### Enum default values
Allows you to define default values when parsing an enum in case the string representation does not map to any of the enum values

Imagine this Enum:
```java
public enum MyEnum {
    one, two, three, other
}
```

And this Gson instance:
```java

Gson gson = new GsonFireBuilder()
    .softEnum(MyEnum.class, MyEnum.other)
    .createGson();
```

If you try to parse, for example, the string `"four"` as the type `MyEnum`, instead of getting `null`, it will be parsed as the `MyEnum.other`

### JsonObject and JsonArray builders
Makes it easy to build json elements without using temporary variables to store them

The following code:
```java
JsonObject builtObject = new JsonObjectBuilder()
    .set("name", "Company, inc")
    .set("offices", new JsonArrayBuilder()
        .add(new JsonObjectBuilder()
            .set("zip", 10001)
            .set("name", "Manhattan")
        )
        .add(new JsonObjectBuilder()
            .set("zip", 11214)
            .set("name", "Brooklyn")
        )
    ).build();
```

Produces the following json:
```json
{
	"name": "Company, inc",
	"offices": [
	    {
		    "zip": 10001,
		    "name": "Manhattan"
	    },
	    {
	    	"zip": 11214,
	    	"name": "Brooklyn"
	    }
	]
}
```

## Using the Gson's GsonBuilder

To access the good old GsonBuilder:

```java
GsonFireBuilder fireBuilder = new GsonFireBuilder();

// Here you can configure the fireBuilder however you want
// and then request a GsonBuilder. The GsonBuilder will be pre-configured
// with all the GsonFire features.

GsonBuilder gsonBuilder = fireBuilder.createGsonBuilder();

// Here you can customize the gsonBuilder using the Gson features and 
// then create a Gson object

Gson gson = gsonBuilder.create()

//The resulting Gson object will have all the GsonFire and Gson features

```

## Release notes

### 1.8.5

- Fixes [#49](https://github.com/julman99/gson-fire/pull/49). Fraction of second incorrect writing. Thanks [jpedrofigueiredo](https://github.com/jpedrofigueiredo) for the contribution!

### 1.8.4

- Fixes [#40](https://github.com/julman99/gson-fire/issues/40). Fraction of second incorrect parsing. Thanks [@crystalin](https://github.com/crystalin)
- Adds OSGi support [#44](https://github.com/julman99/gson-fire/issues/44). Thanks [@abangerl](https://github.com/abangerl)

### 1.8.3

- Fixes [#39](https://github.com/julman99/gson-fire/issues/39). The `JsonTreeReader` and `JsonTreeWriter` used for the Pre/Post processors was not properly set up to mimic the original `JsonReader` or `JsonWriter`. Thanks [@daw3rd](https://github.com/daw3rd)

### 1.8.2

- Fixes [#34](https://github.com/julman99/gson-fire/issues/34). Hooks were not correctly bubbling up `Exceptions`, but instead they were printing and swallowing 🍔 them. Thanks [@Xyaren](https://github.com/Xyaren)


### 1.8.1

- Fixes a bug in which GsonFire was not honoring the "lenient" flag from the `GsonBuilder`. Thanks [@Groostav](https://github.com/Groostav)


### 1.8.0

- Reusing the instances of `ExclusionByValueStrategy` instead of creating an instance each time it is used. Thanks [@Frotty](https://github.com/Frotty)
- Fix the use of a type selector was preventing the GsonFire featues to be executed on the selected type. Thanks [a-a-davydov](https://github.com/a-a-davydov) and [@rubioz](https://github.com/rubioz)

### 1.7.2

- Breaking change: softParse was renamed to enumDefaultValue
- enumDefaultValue now just decorates Gson's internal enum support

### 1.7.1

- Fix: Soft parsing of enums was breaking when the enums had null values

### 1.7.0

- New feature: Soft parsing of Enums! Thanks [@rubioz](https://github.com/rubioz) for the idea!

### 1.6.0

- New feature: wrap/unwrap json objects automatically. Thanks [@andressanchez](https://github.com/andressanchez)!
- New feature: Adds builders for JsonObject and JsonArray
- Using Gson 2.7
- Using the new available Gson.getFieldNamingStrategy method instead of reflection

### 1.5.1

- Fixed a bug in which NullPointerExceptions were thrown when using hooks [#25](https://github.com/julman99/gson-fire/issues/25). Thanks [@knezmilos13](https://github.com/knezmilos13)   


### 1.5.0

- Fixed a bug that caused the RFC3339 date serializing not to be thread safe [#24](https://github.com/julman99/gson-fire/issues/24). Thanks [@npanasenko](https://github.com/npanasenko).
- Hook methods now can get injected the source ```JsonElement``` and ```Gson``` objects [#23](https://github.com/julman99/gson-fire/issues/23). Thanks [@molexx](https://github.com/molexx).  

### 1.4.0

- Fixed a bug in which pre-processors could run multiple times when used in conjunction with TypeSelectors.
- Adds support for RFC3339 date (without the time portion). Thanks [@WonderCsabo](https://github.com/WonderCsabo)
- TypeSelectors will now be invoked also on sub-classes of the class they are registered for. Thanks [@iwarapter](https://github.com/iwarapter) 

### 1.3.1

- Fixed a ```NullPointerException``` that could happen when using the exclude by value feature (thanks [@joaocpsantos](https://github.com/joaocpsantos))

### 1.3.0

- Adds the ability to exclude fields depending on its value (thanks [@joaocpsantos](https://github.com/joaocpsantos))
- Optimized the way objects are explored with reflection

### 1.2.0

- Adds non-anonymous ```Iterable``` called ```SimpleIterable``` that can be serialized and de-serialized.

### 1.1.0

- Adds ```GsonFireBuilder.addSerializationExclusionStrategy``` (thanks [@lalpert](https://github.com/lalpert))

### 1.0.1

- Renames project package to ```io.gsonfire```
- Gson on Fire is now at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.gsonfire%22)
- Deprecates the **Merge Map** functionality in favor of **Post Processors**

### 0.11.0

- Fixes a bug in which GsonFire was not running the type adapters in the same order as class hierarchy.

### 0.10.0

- Adds Pre Processors!

## Usage

Gson on Fire is now at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.gsonfire%22). This means that there is no need to add the [extra maven repository](http://goo.gl/Yctj4r) anymore.

### Maven
Add to your ```pom.xml```

```xml
<dependencies>
    <dependency>
      <groupId>io.gsonfire</groupId>
      <artifactId>gson-fire</artifactId>
      <version>1.8.5</version>
    </dependency>
</dependencies>
```

### Gradle
Add to your ```build.gradle```

```groovy
dependencies {
    compile 'io.gsonfire:gson-fire:1.8.5'
}
```

### Support this project
**Bitcoin Address:** `14LFRrMX3HmyAH9zQsnzYoVKDH6bVWiBu3`
