package io.gsonfire.annotations;

import com.google.gson.TypeAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FieldTypeAdapter {

    Class<? extends TypeAdapter> value();

}
