package cn.jsms.api.sign;

import cn.jsms.api.common.model.IModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DefaultSignPayload implements IModel {

    private static String ID = "id";
    private static String NAME = "name";
    private int id;
    private String name;
    private static Gson gson = new Gson();

    private DefaultSignPayload(Builder builder) {
        id = builder.id;
        name = builder.name;
    }


    public static final class Builder {
        private int id;
        private String name;

        public Builder() {
        }

        public Builder id(int val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public DefaultSignPayload build() {
            return new DefaultSignPayload(this);
        }
    }

    public JsonElement toJSON() {
        JsonObject json = new JsonObject();

        if (id > 0) {
            json.addProperty(ID, id);
        }

        if (name != null) {
            json.addProperty(NAME, name);
        }

        return json;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }
}
