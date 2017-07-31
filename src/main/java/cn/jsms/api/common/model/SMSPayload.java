package cn.jsms.api.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.jiguang.common.utils.TimeUtils;
import cn.jsms.api.schedule.model.RecipientPayload;
import com.google.gson.*;

import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;


public class SMSPayload implements IModel {

	private static String SEND_TIME = "send_time";
	private static String MOBILE = "mobile";
	private static String TEMP_ID = "temp_id";
    private static String TTL = "ttl";
	private static String TEMP_PARA = "temp_para";
    private static String RECIPIENTS = "recipients";

    private String sendTime;
	private String mobile;
	private int temp_id;
    // Time to live parameter, the unit is second.
    private int ttl;
	private final Map<String, String> temp_para;
    private JsonArray recipients;

	private static Gson gson = new Gson();

	private SMSPayload(String mobileNumber, int tempId, int ttl, Map<String, String> temp_para, String sendTime, JsonArray recipients) {
		this.mobile = mobileNumber;
		this.temp_id = tempId;
        this.ttl = ttl;
		this.temp_para = temp_para;
        this.sendTime = sendTime;
        this.recipients = recipients;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String mobile;
		private int temp_id;
        private int ttl;
		private Map<String, String> tempParaBuilder;
        private String sendTime;
        private JsonArray recipients = new JsonArray();

		public Builder setMobileNumber(String mobileNumber) {
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

		public Builder setSendTime(String sendTime) {
            Preconditions.checkArgument(null != sendTime, "send time should not be null.");
            Preconditions.checkArgument(TimeUtils.isDateFormat(sendTime), "The time format is incorrect");
            this.sendTime = sendTime;
            return this;
        }

        public Builder setRecipients(RecipientPayload...recipients) {
            if (recipients == null) {
                return this;
            }

            for (RecipientPayload recipientPayload : recipients) {
                this.recipients.add(recipientPayload.toJSON());
            }
            return this;
        }

        public Builder addRecipient(RecipientPayload recipientPayload) {
            Preconditions.checkArgument(null != recipientPayload, "RecipientPayload should not be null");
            this.recipients.add(recipientPayload.toJSON());
            return this;
        }

		public SMSPayload build() {
			Preconditions.checkArgument(null != mobile, "mobile number should not be null");
			Preconditions.checkArgument(StringUtils.isNotEmpty(mobile), "mobile number should not be empty");
            Preconditions.checkArgument(ttl >= 0, "ttl should not less 0");
            Preconditions.checkArgument(temp_id >= 0, "temp id should not less 0");

			return new SMSPayload(mobile, temp_id, ttl, tempParaBuilder, sendTime, recipients);
		}
	}
	
	public JsonElement toJSON() {
		JsonObject json = new JsonObject();

        if (null != sendTime) {
            json.addProperty(SEND_TIME, sendTime);
        }

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

		if (null != recipients && recipients.size() > 0) {
            json.add(RECIPIENTS, recipients);
        }

		return json;
	}

	@Override
	public String toString() {
		return gson.toJson(toJSON());
	}

	public String getSendTime() {
        return this.sendTime;
    }

	public JsonArray getRecipients() {
        return this.recipients;
    }
}
