package cn.jsms.api;

import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jsms.api.account.AccountBalanceResult;
import cn.jsms.api.account.AppBalanceResult;
import cn.jsms.api.common.JSMSConfig;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.BatchSMSPayload;
import cn.jsms.api.common.model.SMSPayload;
import cn.jsms.api.common.model.BatchSMSResult;
import cn.jsms.api.schedule.model.ScheduleResult;
import cn.jsms.api.schedule.model.ScheduleSMSPayload;
import cn.jsms.api.schedule.model.ScheduleSMSResult;
import cn.jsms.api.sign.DefaultSignPayload;
import cn.jsms.api.sign.SignInfoResult;
import cn.jsms.api.sign.SignPayload;
import cn.jsms.api.sign.SignResult;
import cn.jsms.api.template.SendTempSMSResult;
import cn.jsms.api.template.TempSMSResult;
import cn.jsms.api.template.TemplatePayload;

public class JSMSClient {
	
	private SMSClient _smsClient;
	
	public JSMSClient(String masterSecret, String appkey) {
		_smsClient = new SMSClient(masterSecret, appkey);
	}
	
	public JSMSClient(String masterSecret, String appkey, HttpProxy proxy, JSMSConfig conf) {
		_smsClient = new SMSClient(masterSecret, appkey, proxy, conf);
	}

	public SMSClient getSMSClient() {
		return this._smsClient;
	}

    /**
     * Send SMS verification code to mobile
     * @param payload include two parameters: mobile number and templete id. The second parameter is optional.
     * @return return SendSMSResult which includes msg_id
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
	public SendSMSResult sendSMSCode(SMSPayload payload) 
		throws APIConnectionException, APIRequestException {
		return _smsClient.sendSMSCode(payload);
	}

    /**
     * Send SMS verification code to server, to verify if the code valid
     * @param msgId The message id of the verification code
     * @param code Verification code
     * @return return ValidSMSResult includes is_valid
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
	public ValidSMSResult sendValidSMSCode(String msgId, String code)
		throws APIConnectionException, APIRequestException {
		return _smsClient.sendValidSMSCode(msgId, code);
	}

    /**
     * Send voice SMS verification code to mobile
     * @param payload payload includes two parameters: mobile number and ttl(time to live),
     *                the second one is optional(if miss ttl, will use default value 60 seconds).
     * @return return SendSMSResult which includes msg_id
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
	public SendSMSResult sendVoiceSMSCode(SMSPayload payload)
		throws APIConnectionException, APIRequestException {
		return _smsClient.sendVoiceSMSCode(payload);
	}

    /**
     * Send template SMS to mobile
     * @param payload payload includes mobile, temp_id and temp_para, the temp_para is a map,
     *                which's key is what you had set in jiguang portal
     * @return return SendSMSResult which includes msg_id
     * @throws APIConnectionException  connection exception
     * @throws APIRequestException request exception
     */
	public SendSMSResult sendTemplateSMS(SMSPayload payload)
		throws APIConnectionException, APIRequestException {
		return _smsClient.sendTemplateSMS(payload);
	}

    /**
     * Send a batch of template SMS
     * @param payload BatchSMSPayload
     * @return BatchSMSResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public BatchSMSResult sendBatchTemplateSMS(BatchSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        return _smsClient.sendBatchTemplateSMS(payload);
    }

	/**
	 * Submit a mission that sending a template SMS with pointed schedule
	 * @param payload ScheduleSMSPayload
	 * @return ScheduleResult which includes schedule_id
	 * @throws APIConnectionException connect exception
	 * @throws APIRequestException request exception
	 */
	public ScheduleResult sendScheduleSMS(ScheduleSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        return _smsClient.sendScheduleSMS(payload);
    }

    /**
     * Modify SMS with schedule
     * @param payload ScheduleSMSPayload
     * @param scheduleId id
     * @return ScheduleResult which includes schedule_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ScheduleResult updateScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
            throws APIConnectionException, APIRequestException {
        return _smsClient.updateScheduleSMS(payload, scheduleId);
    }

    /**
     * Submit a mission that sending a batch of SMS with schedule
     * @param payload Payload should include sendTime and recipients
     * @return BatchSMSResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public BatchSMSResult sendBatchScheduleSMS(ScheduleSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        return _smsClient.sendBatchScheduleSMS(payload);
    }

    /**
     * Update batch of SMS with schedule
     * @param payload ScheduleSMSPayload
     * @param scheduleId id
     * @return BatchSMSResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
    public BatchSMSResult updateBatchScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
            throws APIConnectionException, APIRequestException {
        return _smsClient.updateBatchScheduleSMS(payload, scheduleId);
    }

    /**
     * Get schedule SMS by scheduleId
     * @param scheduleId id
     * @return ScheduleSMSResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
    public ScheduleSMSResult getScheduleSMS(String scheduleId) throws APIConnectionException, APIRequestException {
        return _smsClient.getScheduleSMS(scheduleId);
    }

    /**
     * Delete schedule SMS by scheduleId
     * @param scheduleId id
     * @return No content
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ResponseWrapper deleteScheduleSMS(String scheduleId) throws APIConnectionException, APIRequestException {
        return _smsClient.deleteScheduleSMS(scheduleId);
    }

    /**
     * Get account's SMS balance
     * @return AccountBalanceResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public AccountBalanceResult getSMSBalance() throws APIConnectionException, APIRequestException {
        return _smsClient.getSMSBalance();
    }

    /**
     * Get app's SMS balance of an account
     * @return AppBalanceResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public AppBalanceResult getAppSMSBalance() throws APIConnectionException, APIRequestException {
        return _smsClient.getAppSMSBalance();
    }

    //============     Template API     ==============

    /**
     * Create template sms.
     * @param payload {@link TemplatePayload }
     * @return {@link SendTempSMSResult }, include temp_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public SendTempSMSResult createTemplate(TemplatePayload payload) throws APIConnectionException, APIRequestException {
        return _smsClient.createTemplate(payload);
    }

    /**
     * update template sms. Template can be modified ONLY when status is not approved
     * @param payload {@link TemplatePayload }
     * @param tempId temp id
     * @return {@link SendTempSMSResult }, include temp_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public SendTempSMSResult updateTemplate(TemplatePayload payload, int tempId) throws APIConnectionException, APIRequestException {
        return _smsClient.updateTemplate(payload, tempId);
    }

    /**
     * check template by id
     * @param tempId necessary
     * @return {@link TempSMSResult}
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public TempSMSResult checkTemplate(int tempId) throws APIConnectionException, APIRequestException {
        return _smsClient.checkTemplate(tempId);
    }

    /**
     * Delete template by id
     * @param tempId necessary
     * @return No content
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ResponseWrapper deleteTemplate(int tempId) throws APIConnectionException, APIRequestException {
        return _smsClient.deleteTemplate(tempId);
    }

    /**
     * create sign
     * @param payload
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public SignResult createSign(SignPayload payload) throws APIConnectionException, APIRequestException{
        return _smsClient.createSign(payload);
    }

    /**
     * update sign
     * @param payload
     * @param signId
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public SignResult updateSign(SignPayload payload, int signId) throws APIConnectionException, APIRequestException{
        return _smsClient.updateSign(payload, signId);
    }

    /**
     * delete sig by id
     * @param signId
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public ResponseWrapper deleteSign(int signId) throws APIConnectionException, APIRequestException{
        return _smsClient.deleteSign(signId);
    }

    /**
     * get sign by id
     * @param signId
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public SignInfoResult checkSign(int signId) throws APIConnectionException, APIRequestException{
        return _smsClient.checkSign(signId);
    }

//    /**
//     * set default sign
//     * @param payload
//     * @return
//     * @throws APIConnectionException
//     * @throws APIRequestException
//     */
//    public ResponseWrapper setDefaultSign(DefaultSignPayload payload) throws APIConnectionException, APIRequestException{
//        return _smsClient.setDefaultSign(payload);
//    }
}
