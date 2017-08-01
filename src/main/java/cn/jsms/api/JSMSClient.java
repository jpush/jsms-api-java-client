package cn.jsms.api;

import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jsms.api.account.AccountBalanceResult;
import cn.jsms.api.account.AppBalanceResult;
import cn.jsms.api.common.JSMSConfig;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;
import cn.jsms.api.schedule.model.ScheduleListResult;
import cn.jsms.api.schedule.model.ScheduleResult;
import cn.jsms.api.schedule.model.ScheduleSMSPayload;
import cn.jsms.api.schedule.model.ScheduleSMSResult;

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
     * @return ScheduleListResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException request exception
     */
    public ScheduleListResult sendBatchScheduleSMS(ScheduleSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        return _smsClient.sendBatchScheduleSMS(payload);
    }

    /**
     * Update batch of SMS with schedule
     * @param payload ScheduleSMSPayload
     * @param scheduleId id
     * @return ScheduleListResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException request exception
     */
    public ScheduleListResult updateBatchScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
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
}
