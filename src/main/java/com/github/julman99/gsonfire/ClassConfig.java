package com.github.julman99.gsonfire;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @autor: julio
 */
public class ClassConfig<T> {

    private Class<T> clazz;
    private TypeSelector<? super T> typeSelector;
    private Collection<PostProcessor<T>> postProcessors;

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
}
