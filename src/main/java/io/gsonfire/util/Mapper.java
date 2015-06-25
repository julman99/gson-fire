package io.gsonfire.util;

/**
 * Created by julio on 6/24/15.
 */
public interface Mapper<F,T> {

    T map(F from);

}
