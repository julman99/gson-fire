package io.gsonfire.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;

/**
* @autor: julio
*/
final class CollectionOperationTypeAdapter extends TypeAdapter<Collection> {

    private static final JsonElement EMPTY_ARRAY = new JsonArray();
    private final TypeAdapter<Collection> collectionTypeAdapter;

    public CollectionOperationTypeAdapter(TypeAdapter<Collection> collectionTypeAdapter) {
        this.collectionTypeAdapter = collectionTypeAdapter;
    }

    @Override
    public void write(JsonWriter out, Collection value) throws IOException {
        this.collectionTypeAdapter.write(out, value);
    }

    @Override
    public Collection read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        if(token == JsonToken.BEGIN_OBJECT){
            Collection res = this.collectionTypeAdapter.fromJsonTree(EMPTY_ARRAY);
            in.beginObject();
            while(in.hasNext()){
                final Operator op = Operator.valueOf(in.nextName());
                final Collection operand;
                if(op == Operator.$clear){
                    operand = null;
                } else {
                    operand = this.collectionTypeAdapter.read(in);
                }
                op.apply(res, operand);
            }
            in.endObject();
            return res;
        } else {
            return this.collectionTypeAdapter.read(in);
        }
    }

    private enum Operator {
        $add {
            @Override
            public void apply(Collection to, Collection from) {
                to.addAll(from);
            }
        }, $remove {
            @Override
            public void apply(Collection to, Collection from) {
                to.removeAll(from);
            }
        }, $clear {
            @Override
            public void apply(Collection to, Collection from) {
                to.clear();
            }
        };

        public abstract void apply(Collection to, Collection from);
    }
}
