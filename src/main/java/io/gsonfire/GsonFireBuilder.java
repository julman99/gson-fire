package io.gsonfire;

import io.gsonfire.gson.FireTypeAdapterFactory;
import io.gsonfire.postprocessors.MergeMapPostProcessor;
import io.gsonfire.postprocessors.MethodInvokerPostProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

/**
 * @autor: julio
 */
public class GsonFireBuilder {

    private final Map<Class, ClassConfig> classConfigMap = new HashMap<Class, ClassConfig>();
    private final List<Class> orderedClasses = new ArrayList<Class>();

    private DateSerializationPolicy dateSerializationPolicy;
    private TimeZone serializeTimeZone = TimeZone.getDefault();

    private ClassConfig getClassConfig(Class clazz){
        ClassConfig result = classConfigMap.get(clazz);
        if(result == null){
            result = new ClassConfig(clazz);
            classConfigMap.put(clazz, result);
            insertOrdered(orderedClasses, clazz);
        }
        return result;
    }

    private static void insertOrdered(List<Class> classes, Class clazz) {
        for(int i = classes.size() - 1; i >= 0; i--) {
            Class current = classes.get(i);
            if(current.isAssignableFrom(clazz)) {
                classes.add(i + 1, clazz);
                return;
            }
        }
        classes.add(0, clazz);
    }

    /**
     * Registers a Type selector for the Class specified. <br />
     * A type selector is in charge of deciding which sub class to use when converting a json
     * into an object.<br />
     * See <a href="http://goo.gl/qKo7z"> docs and example</a>
     * @param clazz
     * @param factory
     * @param <T>
     * @return
     */
    public <T> GsonFireBuilder registerTypeSelector(Class<T> clazz, TypeSelector<T> factory){
        ClassConfig config = getClassConfig(clazz);
        config.setTypeSelector(factory);
        return this;
    }

    /**
     * Registers a Post processor for the Class specified. <br />
     * A post processor is a class that will add new fields to a generated json just after generation, or that
     * will prepare a class just created from a json.<br />
     * See <a href="http://goo.gl/5fLLN"> docs and example</a>
     *
     * @param clazz
     * @param postProcessor
     * @param <T>
     * @return
     */
    public <T> GsonFireBuilder registerPostProcessor(Class<T> clazz, PostProcessor<? super T> postProcessor){
        ClassConfig config = getClassConfig(clazz);
        config.getPostProcessors().add(postProcessor);
        return this;
    }

    /**
     * Registers a pre processor for the Class specified. <br />
     * A pre processor is a class that will be given the gson to be deserialized in case it wants to change it before
     * it actually gets deserialized into a class
     * See <a href="http://goo.gl/b8V1AA"> docs and example</a>
     *
     * @param clazz
     * @param preProcessor
     * @param <T>
     * @return
     */
    public <T> GsonFireBuilder registerPreProcessor(Class<T> clazz, PreProcessor<? super T> preProcessor){
        ClassConfig config = getClassConfig(clazz);
        config.getPreProcessors().add(preProcessor);
        return this;
    }

    /**
     * Configures the resulting Gson to serialize/unserialize Date instances with a policy
     * @param policy
     * @return
     */
    public GsonFireBuilder dateSerializationPolicy(DateSerializationPolicy policy){
        dateSerializationPolicy = policy;
        return this;
    }

    /**
     * By enabling this, all methods with the annotation {@link io.gsonfire.annotations.ExposeMethodResult} will
     * be evaluated and it result will be added to the resulting json
     * @return
     */
    public GsonFireBuilder enableExposeMethodResult(){
        registerPostProcessor(Object.class, new MethodInvokerPostProcessor<Object>());
        return this;
    }

    /**
     * By enabling this, all methods with the annotation {@link io.gsonfire.annotations.ExposeMethodResult} will
     * be evaluated and it result will be added to the resulting json
     * @return
     */
    public GsonFireBuilder enableHooks(Class clazz){
        ClassConfig config = getClassConfig(clazz);
        config.setHooksEnabled(true);
        return this;
    }

    /**
     * By enabling this, when a class is being converted to Json and it contains a {@link java.util.Map} class
     * annotated with {@link io.gsonfire.annotations.MergeMap}, the map will be walked and merged
     * with the resulting json object
     * @return
     */
    public GsonFireBuilder enableMergeMaps(Class clazz){
        registerPostProcessor(clazz, new MergeMapPostProcessor());
        return this;
    }

    /**
     * Sets the serialization TimeZone. This will affect only values that depend on the TimeZone, for example rfc3339
     * dates.
     * @param timeZone
     * @return
     */
    public GsonFireBuilder serializeTimeZone(TimeZone timeZone) {
        this.serializeTimeZone = timeZone;
        return this;
    }

    /**
     * Returns a new instance of the good old {@link GsonBuilder}
     * @return
     */
    public GsonBuilder createGsonBuilder(){
        GsonBuilder builder = new GsonBuilder();

        for(Class clazz: orderedClasses){
            ClassConfig config = classConfigMap.get(clazz);
            builder.registerTypeAdapterFactory(new FireTypeAdapterFactory(config));
        }

        if(dateSerializationPolicy != null){
            builder.registerTypeAdapter(Date.class, dateSerializationPolicy.createTypeAdapter(serializeTimeZone));
        }

        return builder;
    }

    /**
     * Returns a new {@link Gson} instance
     * @return
     */
    public Gson createGson(){
        return createGsonBuilder().create();
    }
}
