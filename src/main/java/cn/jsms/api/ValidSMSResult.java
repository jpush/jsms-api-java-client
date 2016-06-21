package cn.jsms.api;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import cn.jpush.api.common.resp.BaseResult;

public class ValidSMSResult extends BaseResult {

	@Expose Boolean isValid;
	
	public Boolean getIsValid() {
		return isValid;
	}
	
}
