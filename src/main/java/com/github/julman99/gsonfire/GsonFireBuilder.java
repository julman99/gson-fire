package com.github.julman99.gsonfire;

import com.github.julman99.gsonfire.gson.FireTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor: julio
 */
public class GsonFireBuilder {

    private Map<Class, ClassConfig> classConfigMap = new HashMap<Class, ClassConfig>();
    private DateSerializationPolicy dateSerializationPolicy;

    private ClassConfig getClassConfig(Class clazz){
        ClassConfig result = classConfigMap.get(clazz);
        if(result == null){
            result = new ClassConfig(clazz);
            classConfigMap.put(clazz, result);
        }
        return result;
    }

    public <T> GsonFireBuilder registerTypeSelector(Class<T> t, TypeSelector<T> factory){
        ClassConfig config = getClassConfig(t);
        config.setTypeSelector(factory);
        return this;
    }

    public <T> GsonFireBuilder registerPostProcessor(Class<T> clazz, PostProcessor<? super T> postProcessor){
        ClassConfig config = getClassConfig(clazz);
        config.getPostProcessors().add(postProcessor);
        return this;
    }

    public <T> GsonFireBuilder dateSerializationPolicy(DateSerializationPolicy policy){
        dateSerializationPolicy = policy;
        return this;
    }

    public GsonBuilder createGsonBuilder(){
        GsonBuilder builder = new GsonBuilder();

        for(ClassConfig config: classConfigMap.values()){
            builder.registerTypeAdapterFactory(new FireTypeAdapterFactory(config));
        }

        if(dateSerializationPolicy != null){
            builder.registerTypeAdapter(Date.class, dateSerializationPolicy.createTypeAdapter());
        }

        return builder;
    }

    public Gson createGson(){
        return createGsonBuilder().create();
    }
}
