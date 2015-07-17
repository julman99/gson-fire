package io.gsonfire.gson;

public interface ExclusionByValueStrategy<T> {

    boolean shouldSkipField(T fieldValue);

}
