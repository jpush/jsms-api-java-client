package cn.jsms.api;

import cn.jiguang.common.connection.HttpProxy;
import cn.jsms.api.common.JSMSConfig;
import cn.jsms.api.common.SMSClient;

public class JSMSClient {
	
	private SMSClient _smsClient;
	
	public JSMSClient(String masterSecret, String appkey, HttpProxy proxy, JSMSConfig conf) {
		_smsClient = new SMSClient(masterSecret, appkey, proxy, conf);
	}
}
