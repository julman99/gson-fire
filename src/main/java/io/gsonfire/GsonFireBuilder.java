package io.gsonfire;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.gsonfire.annotations.Wrap;
import io.gsonfire.gson.*;
import io.gsonfire.postprocessors.MergeMapPostProcessor;
import io.gsonfire.postprocessors.methodinvoker.MethodInvokerPostProcessor;
import io.gsonfire.util.reflection.FieldInspector;

import java.util.*;

/**
 * @autor: julio
 */
public final class GsonFireBuilder {

    private final Map<Class, ClassConfig> classConfigMap = new HashMap<Class, ClassConfig>();
    private final List<Class> orderedClasses = new ArrayList<Class>();
    private final List<FireExclusionStrategy> serializationExclusions = new ArrayList<FireExclusionStrategy>();
    private final FieldInspector fieldInspector = new FieldInspector();

    private DateSerializationPolicy dateSerializationPolicy;
    private boolean dateDeserializationStrict = true;
    private TimeZone serializeTimeZone = TimeZone.getDefault();
    private boolean enableExposeMethodResults = false;
    private boolean enableWrappedClasses = false;
    private boolean enableExclusionByValueStrategies = false;

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
        this.enableExposeMethodResults = true;
        return this;
    }

    /**
     * By enabling this, all exclusion by value strategies specified with the annotation
     * {@link io.gsonfire.annotations.ExcludeByValue} will be run to remove specific fields from the resulting json
     * @return
     */
    public GsonFireBuilder enableExclusionByValue(){
        this.enableExclusionByValueStrategies = true;
        return this;
    }

    /**
     * By enabling this, all classes with the annotation {@link Wrap}
     * will be wrapped/unwrapped when serialization/deserialization.
     * @return
     */
    public GsonFireBuilder enableWrappedClasses(){
        this.enableWrappedClasses = true;
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
     * with the resulting json object.
     *
     * This method has been deprecated and a {@link io.gsonfire.PostProcessor} should be used instead
     * @return
     */
    @Deprecated
    public GsonFireBuilder enableMergeMaps(Class clazz){
        registerPostProcessor(clazz, new MergeMapPostProcessor(fieldInspector));
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

    public GsonFireBuilder addSerializationExclusionStrategy(FireExclusionStrategy exclusionStrategy) {
        this.serializationExclusions.add(exclusionStrategy);
        return this;
    }

    /**
     * Returns a new instance of the good old {@link GsonBuilder}
     * @return
     */
    public GsonBuilder createGsonBuilder(){
        GsonBuilder builder = new GsonBuilder();

        if(enableExposeMethodResults) {
            FireExclusionStrategy compositeExclusionStrategy = new FireExclusionStrategyComposite(serializationExclusions);
            registerPostProcessor(Object.class, new MethodInvokerPostProcessor<Object>(compositeExclusionStrategy));
        }

        if(enableExclusionByValueStrategies) {
            builder.registerTypeAdapterFactory(new ExcludeByValueTypeAdapterFactory(fieldInspector));
        }

        if (enableWrappedClasses) {
            builder.registerTypeAdapterFactory(new WrapTypeAdapterFactory());
        }

        for(Class clazz: orderedClasses){
            ClassConfig config = classConfigMap.get(clazz);
            if(config.getTypeSelector() != null) {
                builder.registerTypeAdapterFactory(new TypeSelectorTypeAdapterFactory(config));
            }
            builder.registerTypeAdapterFactory(new FireTypeAdapterFactory(config));
        }

        if(dateSerializationPolicy != null){
            builder.registerTypeAdapter(Date.class, dateSerializationPolicy.createTypeAdapter(serializeTimeZone));
        }

        builder.registerTypeAdapterFactory(new SimpleIterableTypeAdapterFactory());

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
