package cn.jsms.api;

import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.common.JSMSConfig;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;

public class JSMSClient {
	
	private SMSClient _smsClient;
	
	public JSMSClient(String masterSecret, String appkey, HttpProxy proxy, JSMSConfig conf) {
		_smsClient = new SMSClient(masterSecret, appkey, proxy, conf);
	}
	
	public SendSMSResult sendSMSCode(SMSPayload payload) 
		throws APIConnectionException, APIRequestException {
		return _smsClient.sendSMSCode(payload);
	}
	
	public ValidSMSResult sendValidSMSCode(String msgId, int code) 
		throws APIConnectionException, APIRequestException {
		return _smsClient.sendValidSMSCode(msgId, code);
	}
}
