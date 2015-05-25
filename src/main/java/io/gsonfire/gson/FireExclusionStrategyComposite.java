package io.gsonfire.gson;

import com.google.gson.FieldAttributes;
import io.gsonfire.postprocessors.MappedMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by julio on 5/25/15.
 */
public class FireExclusionStrategyComposite implements FireExclusionStrategy {

    private final Collection<FireExclusionStrategy> strategies;

    public FireExclusionStrategyComposite(FireExclusionStrategy... strategies) {
        this(Arrays.asList(strategies));
    }

    public FireExclusionStrategyComposite(Collection<FireExclusionStrategy> strategies) {
        this.strategies = new ArrayList<FireExclusionStrategy>(strategies);
    }

    @Override
    public boolean shouldSkipMethod(MappedMethod method) {
        for(FireExclusionStrategy strategy: strategies) {
            if(strategy.shouldSkipMethod(method)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        for(FireExclusionStrategy strategy: strategies) {
            if(strategy.shouldSkipField(f)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        for(FireExclusionStrategy strategy: strategies) {
            if(strategy.shouldSkipClass(clazz)) {
                return true;
            }
        }
        return false;
    }
}
