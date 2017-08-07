package cn.jsms.api.common.model;

import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;
import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

public class RecipientPayload implements IModel {

    private final static String MOBILE = "mobile";
    private final static String TEMP_PARA = "temp_para";

    private String mobile;
    private Map<String, String> temp_para;
    private Gson gson = new Gson();

    public RecipientPayload(String mobile, Map<String, String> tempPara) {
        this.mobile = mobile;
        this.temp_para = tempPara;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String mobile;
        private Map<String, String> tempPara;

        public Builder setMobile(String mobileNumber) {
            this.mobile = mobileNumber.trim();
            return this;
        }

        public Builder setTempPara(Map<String, String> temp_para) {
            Preconditions.checkArgument(! (null == temp_para), "temp_para should not be null.");
            if (null == tempPara) {
                tempPara = new HashMap<String, String>();
            }
            for (String key : temp_para.keySet()) {
                tempPara.put(key, temp_para.get(key));
            }
            return this;
        }

        public Builder addTempPara(String key, String value) {
            Preconditions.checkArgument(! (null == key || null == value), "Key/Value should not be null.");
            if (null == tempPara) {
                tempPara = new HashMap<String, String>();
            }
            tempPara.put(key, value);
            return this;
        }

        public RecipientPayload build() {
            Preconditions.checkArgument(null != mobile, "mobile number should not be null");
            Preconditions.checkArgument(StringUtils.isNotEmpty(mobile), "mobile number should not be empty");

            return new RecipientPayload(mobile, tempPara);
        }
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();
        if (null != mobile) {
            jsonObject.addProperty(MOBILE, mobile);
        }

        JsonObject tempJson = null;
        if (null != temp_para) {
            tempJson = new JsonObject();
            for (String key : temp_para.keySet()) {
                if (temp_para.get(key) != null) {
                    tempJson.add(key, new JsonPrimitive(temp_para.get(key)));
                } else {
                    tempJson.add(key, JsonNull.INSTANCE);
                }
            }
        }

        if (null != tempJson) {
            jsonObject.add(TEMP_PARA, tempJson);
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }
}
