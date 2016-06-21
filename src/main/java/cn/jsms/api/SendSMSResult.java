package cn.jsms.api;

import com.google.gson.annotations.Expose;

import cn.jpush.api.common.resp.BaseResult;

public class SendSMSResult extends BaseResult {
	
	@Expose Long msg_id;
	
	public Long getMessageId() {
		return msg_id;
	}

}
