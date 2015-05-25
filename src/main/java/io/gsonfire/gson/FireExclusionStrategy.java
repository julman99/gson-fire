package io.gsonfire.gson;

import com.google.gson.ExclusionStrategy;
import io.gsonfire.postprocessors.MappedMethod;

/**
 * Created by julio on 5/25/15.
 */
public interface FireExclusionStrategy extends ExclusionStrategy {

    boolean shouldSkipMethod(MappedMethod method);

}
