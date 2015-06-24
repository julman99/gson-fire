package io.gsonfire;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @autor: julio
 */
public final class ClassConfig<T> {

    private Class<T> clazz;
    private TypeSelector<? super T> typeSelector;
    private Collection<PreProcessor<T>> preProcessors;
    private Collection<PostProcessor<T>> postProcessors;
    private boolean hooksEnabled;

    public ClassConfig(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getConfiguredClass(){
        return clazz;
    }

    public TypeSelector<? super T> getTypeSelector() {
        return typeSelector;
    }

    public void setTypeSelector(TypeSelector<? super T> typeSelector) {
        this.typeSelector = typeSelector;
    }

    public Collection<PostProcessor<T>> getPostProcessors() {
        if(postProcessors == null){
            postProcessors = new ArrayList<PostProcessor<T>>();
        }
        return postProcessors;
    }

    public Collection<PreProcessor<T>> getPreProcessors() {
        if(preProcessors == null){
            preProcessors = new ArrayList<PreProcessor<T>>();
        }
        return preProcessors;
    }

    public boolean isHooksEnabled() {
        return hooksEnabled;
    }

    public void setHooksEnabled(boolean hooksEnabled) {
        this.hooksEnabled = hooksEnabled;
    }
}
