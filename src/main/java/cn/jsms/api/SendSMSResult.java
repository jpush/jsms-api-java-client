package cn.jsms.api;

import com.google.gson.annotations.Expose;

import cn.jiguang.common.resp.BaseResult;

public class SendSMSResult extends BaseResult {
	
	@Expose String msg_id;
	
	public String getMessageId() {
		return msg_id;
	}

}
