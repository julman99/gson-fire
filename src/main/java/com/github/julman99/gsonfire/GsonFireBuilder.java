package com.github.julman99.gsonfire;

import com.github.julman99.gsonfire.gson.PostProcessorTypeAdapterFactory;
import com.github.julman99.gsonfire.gson.TypeSelectorTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * @autor: julio
 */
public class GsonFireBuilder {

    private GsonBuilder builder = new GsonBuilder();

    public <T> GsonFireBuilder registerTypeSelector(Class<T> t, TypeSelector<T> factory){
        builder.registerTypeAdapterFactory(new TypeSelectorTypeAdapterFactory(t, factory));
        return this;
    }

    public <T> GsonFireBuilder registerPostProcessor(Class<T> clazz, PostProcessor<? super T> postProcessor){
        builder.registerTypeAdapterFactory(new PostProcessorTypeAdapterFactory(clazz, postProcessor));
        return this;
    }

    public <T> GsonFireBuilder dateSerializationPolicy(DateSerializationPolicy policy){
        builder.registerTypeAdapter(Date.class, policy.createTypeAdapter());
        return this;
    }

    public GsonBuilder createGsonBuilder(){
        return builder;
    }

    public Gson createGson(){
        return builder.create();
    }
}
