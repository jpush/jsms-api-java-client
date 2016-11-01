package cn.jsms.api.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;
import com.google.gson.JsonPrimitive;


public class SMSPayload implements IModel {

	private static String MOBILE = "mobile";
	private static String TEMP_ID = "temp_id";
    private static String TTL = "ttl";
	private static String TEMP_PARA = "temp_para";

	private String mobile;
	private int temp_id;
    // Time to live parameter, the unit is second.
    private int ttl;
	private final Map<String, String> temp_para;

	private static Gson gson = new Gson();

	private SMSPayload(String mobileNumber, int tempId, int ttl, Map<String, String> temp_para) {
		this.mobile = mobileNumber;
		this.temp_id = tempId;
        this.ttl = ttl;
		this.temp_para = temp_para;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String mobile;
		private int temp_id;
        private int ttl;
		private Map<String, String> tempParaBuilder;

		public Builder setMobildNumber(String mobileNumber) {
			this.mobile = mobileNumber.trim();
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

			return new SMSPayload(mobile, temp_id, ttl, tempParaBuilder);
		}
	}
	
	public JsonElement toJSON() {
		JsonObject json = new JsonObject();

		if (null != mobile) {
			json.addProperty(MOBILE, mobile);
		}

		if (temp_id > 0) {
            json.addProperty(TEMP_ID, temp_id);
        }

		if (ttl > 0) {
            json.addProperty(TTL, ttl);
        }

		JsonObject tempJson = null;
		if (null != temp_para) {
			tempJson = new JsonObject();
			for (String key : temp_para.keySet()) {
				tempJson.add(key, new JsonPrimitive(temp_para.get(key)));
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
