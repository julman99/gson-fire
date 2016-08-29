package io.gsonfire.builders;

import com.google.gson.JsonElement;

/**
 * Created by julio on 8/29/16.
 */
public interface JsonElementBuilder<T extends JsonElement> {

    T build();

}
