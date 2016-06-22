package cn.jsms.api.common.model;

import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.jiguang.commom.utils.Preconditions;
import cn.jiguang.commom.utils.StringUtils;


public class SMSPayload implements IModel {

	private static String MOBILE = "mobile";
	private static String TEMP_ID = "temp_id";

	private String mobile;
	private int temp_id;

	private static Gson gson = new Gson();
	private static final Pattern MOBILE_PATTERN = Pattern.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$");

	public SMSPayload(String mobileNumber, int tempId) {
		this.mobile = mobileNumber;
		this.temp_id = tempId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String mobile;
		private int temp_id;

		public Builder setMobildNumber(String mobileNumber) {
			this.mobile = mobileNumber.trim();
			return this;
		}

		public Builder setTempId(int tempId) {
			this.temp_id = tempId;
			return this;
		}

		public SMSPayload build() {
			Preconditions.checkArgument(null != mobile, "mobile number should not be null");
			Preconditions.checkArgument(StringUtils.isNotEmpty(mobile), "mobile number should not be empty");
			
			byte[] mobileByte = mobile.getBytes();
			if (mobileByte.length != 11) {
				throw new IllegalArgumentException("The length of mobile equals 11. Input is " + mobile);
			}
			Preconditions.checkArgument(checkMobile(mobile), "invalid mobile number, please check again");
			
			Preconditions.checkArgument(temp_id > 0, "temp id should not less 0");
			return new SMSPayload(mobile, temp_id);
		}
	}
	
	public static boolean checkMobile(String mobile) {
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

	public JsonElement toJSON() {
		JsonObject json = new JsonObject();

		if (null != mobile) {
			json.addProperty(MOBILE, mobile);
		}

		if (-1 != temp_id) {
			json.addProperty(TEMP_ID, temp_id);
		}

		return json;
	}

	@Override
	public String toString() {
		return gson.toJson(toJSON());
	}

}
