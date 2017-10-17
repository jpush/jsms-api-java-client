package cn.jsms.api.template;

import java.util.HashMap;
import java.util.Map;

import cn.jsms.api.common.model.IModel;
import com.google.gson.*;

import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;


public class TemplatePayload implements IModel {

    private static String TEMPLATE = "template";
    private static String TTL = "ttl";
    private static String TYPE = "type";
    private static String REMARK = "remark";

    private String template;
    private int type;
    // Time to live parameter, the unit is second.
    private int ttl;
    private String remark;

    private static Gson gson = new Gson();

    private TemplatePayload(String template, int type, int ttl, String remark) {
        this.template = template;
        this.type = type;
        this.ttl = ttl;
        this.remark = remark;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String template;
        private int type;
        private int ttl;
        private String remark;

        public Builder setTemplate(String template) {
            this.template = template;
            return this;
        }

        public Builder setTTL(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public TemplatePayload build() {
            Preconditions.checkArgument(ttl >= 0, "ttl should not less 0");
            Preconditions.checkArgument(type > 0, "type should be 1, or 2 or 3");

            return new TemplatePayload(template, type, ttl, remark);
        }
    }

    public JsonElement toJSON() {
        JsonObject json = new JsonObject();

        if (template != null) {
            json.addProperty(TEMPLATE, template);
        }

        if (type > 0) {
            json.addProperty(TYPE, type);
        }

        if (ttl > 0) {
            json.addProperty(TTL, ttl);
        }

        if (remark != null) {
            json.addProperty(REMARK, remark);
        }
        return json;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }
}
