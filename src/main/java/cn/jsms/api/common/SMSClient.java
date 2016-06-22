package cn.jsms.api.common;

import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import cn.jiguang.commom.ServiceHelper;
import cn.jiguang.commom.utils.Preconditions;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.connection.NativeHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.ValidSMSResult;
import cn.jsms.api.common.model.SMSPayload;

public class SMSClient {

	private static String SMS_CODE = "code";
	
	private String _baseUrl;
	private String _smsPath;
	private String _validPath;
	private NativeHttpClient _httpClient;

	public SMSClient(String masterSecret, String appkey, HttpProxy proxy, JSMSConfig conf) {
		ServiceHelper.checkBasic(appkey, masterSecret);
	
		_baseUrl = (String) conf.get(JSMSConfig.API_HOST_NAME);
		_smsPath = (String) conf.get(JSMSConfig.CODE_PATH);
		_validPath = (String) conf.get(JSMSConfig.VALID_PATH);
		
		String authCode = ServiceHelper.getBasicAuthorization(appkey, masterSecret);
        this._httpClient = new NativeHttpClient(authCode, proxy, conf.getClientConfig());
	}
	
	/**
	 * Send SMS verification code to mobile
	 * @param payload include two parameters: mobile number and templete id.
	 * @return
	 * @throws APIConnectionException
	 * @throws APIRequestException
	 */
	public SendSMSResult sendSMSCode(SMSPayload payload) 
		throws APIConnectionException, APIRequestException {
		Preconditions.checkArgument(null != payload, "SMS payload should not be null");
		
		ResponseWrapper response = _httpClient.sendPost(_baseUrl + _smsPath, payload.toString());
		return SendSMSResult.fromResponse(response, SendSMSResult.class);
	}
	
	/**
	 * Send SMS verification code to server, to verify if the code valid 
	 * @param msgId The message id of the verification code  
	 * @param code Verification code
	 * @return
	 * @throws APIConnectionException
	 * @throws APIRequestException
	 */
	public ValidSMSResult sendValidSMSCode(String msgId, int code)
		throws APIConnectionException, APIRequestException {
		Preconditions.checkArgument(null != msgId, "Message id should not be null");
		String codeStr = String.valueOf(code);
		Pattern codePattern = Pattern.compile("^[0-9]{6}");
		Preconditions.checkArgument(codePattern.matcher(codeStr).matches(), "The verification code shoude be consist of six number");
		JsonObject json = new JsonObject();
		json.addProperty(SMS_CODE, code);
		
		ResponseWrapper response = _httpClient.sendPost(_baseUrl + _smsPath + "/" + msgId + _validPath, json.toString());
		return ValidSMSResult.fromResponse(response, ValidSMSResult.class);
	}
}
