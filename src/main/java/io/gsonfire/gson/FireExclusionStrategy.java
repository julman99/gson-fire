package io.gsonfire.gson;

import io.gsonfire.postprocessors.MappedMethod;

/**
 * Created by julio on 5/25/15.
 */
public interface FireExclusionStrategy {

    boolean shouldSkipMethod(MappedMethod method);

}
