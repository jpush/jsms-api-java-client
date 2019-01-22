package cn.jsms.api.common.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;

import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;


public class SMSPayload implements IModel {

    private static String MOBILE = "mobile";
    private static String SIGN_ID = "sign_id";
    private static String TEMP_ID = "temp_id";
    private static String TTL = "ttl";
    private static String TEMP_PARA = "temp_para";
    private static String CODE = "code";
    private static String VOICE_LANG = "voice_lang";

    private String mobile;
    private int sign_id;
    private int temp_id;
    // Time to live parameter, the unit is second.
    private int ttl;
    private String code;
    private int voice_lang = -1;
    private final Map<String, String> temp_para;
    private static Gson gson = new Gson();

    private SMSPayload(String mobileNumber, int signId, int tempId, int ttl, String code, int voiceLang, Map<String, String> temp_para) {
        this.mobile = mobileNumber;
        this.sign_id = signId;
        this.temp_id = tempId;
        this.ttl = ttl;
        this.code = code;
        this.voice_lang = voiceLang;
        this.temp_para = temp_para;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String mobile;
        private int sign_id;
        private int temp_id;
        private int ttl;
        private String code;
        private int voice_lang = -1;
        private Map<String, String> tempParaBuilder;

        public Builder setMobileNumber(String mobileNumber) {
            this.mobile = mobileNumber.trim();
            return this;
        }

        public Builder setSignId(int signId) {
            this.sign_id= signId;
            return this;
        }

        public Builder setTempId(int tempId) {
            this.temp_id = tempId;
            return this;
        }

        public Builder setTTL(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder setCode(String code) {
            Preconditions.checkArgument(code.length() >= 4 && code.length() <= 8, "Code's length should between 4 and 8");
            this.code = code;
            return this;
        }

        public Builder setVoiceLang(int voiceLang) {
            Preconditions.checkArgument(voiceLang == 0 || voiceLang == 1 || voiceLang == 2, "Illegal voice lang. Voice lang should be 0, or 1 or 2");
            this.voice_lang = voiceLang;
            return this;
        }

        public Builder setTempPara(Map<String, String> temp_para) {
            Preconditions.checkArgument(! (null == temp_para), "temp_para should not be null.");
            if (null == tempParaBuilder) {
                tempParaBuilder = new HashMap<String, String>();
            }
            for (String key : temp_para.keySet()) {
                tempParaBuilder.put(key, temp_para.get(key));
            }
            return this;
        }

        public Builder addTempPara(String key, String value) {
            Preconditions.checkArgument(! (null == key || null == value), "Key/Value should not be null.");
            if (null == tempParaBuilder) {
                tempParaBuilder = new HashMap<String, String>();
            }
            tempParaBuilder.put(key, value);
            return this;
        }

        public SMSPayload build() {
            Preconditions.checkArgument(null != mobile, "mobile number should not be null");
            Preconditions.checkArgument(StringUtils.isNotEmpty(mobile), "mobile number should not be empty");
            Preconditions.checkArgument(ttl >= 0, "ttl should not less 0");
            Preconditions.checkArgument(temp_id >= 0, "temp id should not less 0");

            return new SMSPayload(mobile, sign_id, temp_id, ttl, code, voice_lang, tempParaBuilder);
        }
    }

    public JsonElement toJSON() {
        JsonObject json = new JsonObject();

        if (null != mobile) {
            json.addProperty(MOBILE, mobile);
        }

        if (sign_id > 0) {
            json.addProperty(SIGN_ID, sign_id);
        }

        if (temp_id > 0) {
            json.addProperty(TEMP_ID, temp_id);
        }

        if (ttl > 0) {
            json.addProperty(TTL, ttl);
        }

        if (voice_lang != -1) {
            json.addProperty(VOICE_LANG, voice_lang);
        }

        if (code != null) {
            json.addProperty(CODE, code);
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
            json.add(TEMP_PARA, tempJson);
        }

        return json;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }
}
