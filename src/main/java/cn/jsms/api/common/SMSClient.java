package cn.jsms.api.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import cn.jiguang.common.connection.IHttpClient;
import cn.jiguang.common.connection.NettyHttpClient;
import cn.jiguang.common.utils.StringUtils;
import cn.jsms.api.account.AccountBalanceResult;
import cn.jsms.api.account.AppBalanceResult;
import cn.jsms.api.common.model.BatchSMSPayload;
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
import com.google.gson.JsonObject;

import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.connection.HttpProxy;
import cn.jiguang.common.connection.NativeHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.ValidSMSResult;
import cn.jsms.api.common.model.SMSPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

public class SMSClient {

    private static String SMS_CODE = "code";
    private final String NEWLINE = "\r\n";
    private final String BOUNDARY = "========7d4a6d158c9";
    private static Logger LOG = LoggerFactory.getLogger(SMSClient.class);

    private String _baseUrl;
    private String _smsCodePath;
    private String _validPath;
    private String _voiceCodePath;
    private String _shortMsgPath;
    private String _schedulePath;
    private String _signPath;
    private String _signDefaultPath;
    private String _accountPath;
    private String _tempMsgPath;
    private IHttpClient _httpClient;
    private String _authCode;

    public SMSClient(String masterSecret, String appkey) {
        this(masterSecret, appkey, null, JSMSConfig.getInstance());
    }

    public SMSClient(String masterSecret, String appkey, HttpProxy proxy, JSMSConfig conf) {
        ServiceHelper.checkBasic(appkey, masterSecret);

        _baseUrl = (String) conf.get(JSMSConfig.API_HOST_NAME);
        _smsCodePath = (String) conf.get(JSMSConfig.CODE_PATH);
        _validPath = (String) conf.get(JSMSConfig.VALID_PATH);
        _voiceCodePath = (String) conf.get(JSMSConfig.VOICE_CODE_PATH);
        _shortMsgPath = (String) conf.get(JSMSConfig.SHORT_MESSAGE_PATH);
        _tempMsgPath = (String) conf.get(JSMSConfig.TEMPlATE_MESSAGE_PATH);
        _signPath = (String) conf.get(JSMSConfig.SIGN_PATH);
        _signDefaultPath = (String) conf.get(JSMSConfig.SIGN_DEFAULT_PATH);
        _schedulePath = (String) conf.get(JSMSConfig.SCHEDULE_PATH);
        _accountPath = (String) conf.get(JSMSConfig.ACCOUNT_PATH);
        String authCode = ServiceHelper.getBasicAuthorization(appkey, masterSecret);
        _authCode = authCode;
        this._httpClient = new NativeHttpClient(authCode, proxy, conf.getClientConfig());
    }

    /**
     * Send SMS verification code to mobile
     *
     * @param payload include two parameters: mobile number and templete id. The second parameter is optional.
     * @return return SendSMSResult which includes msg_id
     * @throws APIConnectionException connection exception
     * @throws APIRequestException    request exception
     */
    public SendSMSResult sendSMSCode(SMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "SMS payload should not be null");

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _smsCodePath, payload.toString());
        return SendSMSResult.fromResponse(response, SendSMSResult.class);
    }

    /**
     * Send SMS verification code to server, to verify if the code valid
     *
     * @param msgId The message id of the verification code
     * @param code  Verification code
     * @return return ValidSMSResult includes is_valid
     * @throws APIConnectionException connection exception
     * @throws APIRequestException    request exception
     */
    public ValidSMSResult sendValidSMSCode(String msgId, String code)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != msgId, "Message id should not be null");
        Pattern codePattern = Pattern.compile("^[0-9]{6}");
        Preconditions.checkArgument(codePattern.matcher(code).matches(), "The verification code shoude be consist of six number");
        JsonObject json = new JsonObject();
        json.addProperty(SMS_CODE, code);

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _smsCodePath + "/" + msgId + _validPath, json.toString());
        return ValidSMSResult.fromResponse(response, ValidSMSResult.class);
    }

    /**
     * Send voice SMS verification code to mobile
     *
     * @param payload payload includes two parameters: mobile number and ttl(time to live),
     *                the second one is optional(if miss ttl, will use default value 60 seconds).
     * @return return SendSMSResult which includes msg_id
     * @throws APIConnectionException connection exception
     * @throws APIRequestException    request exception
     */
    public SendSMSResult sendVoiceSMSCode(SMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "SMS payload should not be null");

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _voiceCodePath, payload.toString());
        return SendSMSResult.fromResponse(response, SendSMSResult.class);
    }

    /**
     * Send template SMS to mobile
     *
     * @param payload payload includes mobile, temp_id and temp_para, the temp_para is a map,
     *                which's key is what you had set in jiguang portal
     * @return return SendSMSResult which includes msg_id
     * @throws APIConnectionException connection exception
     * @throws APIRequestException    request exception
     */
    public SendSMSResult sendTemplateSMS(SMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "SMS payload should not be null");

        ResponseWrapper response = _httpClient.sendPost(_baseUrl + _shortMsgPath, payload.toString());
        return SendSMSResult.fromResponse(response, SendSMSResult.class);
    }

    /**
     * Send a batch of template SMS
     *
     * @param payload BatchSMSPayload
     * @return BatchSMSResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public BatchSMSResult sendBatchTemplateSMS(BatchSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "BatchSMSPayload should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _shortMsgPath + "/batch", payload.toString());
        return BatchSMSResult.fromResponse(responseWrapper, BatchSMSResult.class);
    }

    public void setHttpClient(IHttpClient client) {
        this._httpClient = client;
    }

    // 如果使用 NettyHttpClient，在发送请求后需要手动调用 close 方法
    public void close() {
        if (_httpClient != null && _httpClient instanceof NettyHttpClient) {
            ((NettyHttpClient) _httpClient).close();
        }
    }

    /**
     * Submit a mission that sending a template SMS with pointed schedule
     *
     * @param payload ScheduleSMSPayload
     * @return ScheduleResult which includes schedule_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public ScheduleResult sendScheduleSMS(ScheduleSMSPayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != payload.getMobile(), "Mobile should not be null");
        Preconditions.checkArgument(StringUtils.isMobileNumber(payload.getMobile()), "Invalid mobile number");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _schedulePath, payload.toString());
        return ScheduleResult.fromResponse(responseWrapper, ScheduleResult.class);
    }

    /**
     * Modify SMS with schedule
     *
     * @param payload    ScheduleSMSPayload
     * @param scheduleId id
     * @return ScheduleResult which includes schedule_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public ScheduleResult updateScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        Preconditions.checkArgument(null != payload.getMobile(), "Mobile should not be null");
        Preconditions.checkArgument(StringUtils.isMobileNumber(payload.getMobile()), "Invalid mobile number");
        ResponseWrapper responseWrapper = _httpClient.sendPut(_baseUrl + _schedulePath + "/" + scheduleId, payload.toString());
        return ScheduleResult.fromResponse(responseWrapper, ScheduleResult.class);
    }

    /**
     * Submit a mission that sending a batch of SMS with schedule
     *
     * @param payload Payload should include sendTime and recipients
     * @return BatchSMSResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public BatchSMSResult sendBatchScheduleSMS(ScheduleSMSPayload payload)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != payload.getRecipients(), "Recipients should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _schedulePath + "/batch", payload.toString());
        return BatchSMSResult.fromResponse(responseWrapper, BatchSMSResult.class);
    }

    /**
     * Update batch of SMS with schedule
     *
     * @param payload    ScheduleSMSPayload
     * @param scheduleId id
     * @return BatchSMSResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException    request exception
     */
    public BatchSMSResult updateBatchScheduleSMS(ScheduleSMSPayload payload, String scheduleId)
            throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Schedule SMS payload should not be null");
        Preconditions.checkArgument(null != payload.getRecipients(), "Recipients should not be null");
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPut(_baseUrl + _schedulePath + "/batch/" + scheduleId, payload.toString());
        return BatchSMSResult.fromResponse(responseWrapper, BatchSMSResult.class);
    }

    /**
     * Get schedule SMS by scheduleId
     *
     * @param scheduleId id
     * @return ScheduleSMSResult
     * @throws APIConnectionException connection exception
     * @throws APIRequestException    request exception
     */
    public ScheduleSMSResult getScheduleSMS(String scheduleId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _schedulePath + "/" + scheduleId);
        return ScheduleSMSResult.fromResponse(responseWrapper, ScheduleSMSResult.class);
    }

    /**
     * Delete schedule SMS by scheduleId
     *
     * @param scheduleId id
     * @return No content
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public ResponseWrapper deleteScheduleSMS(String scheduleId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != scheduleId, "Schedule id should not be null");
        return _httpClient.sendDelete(_baseUrl + _schedulePath + "/" + scheduleId);
    }

    /**
     * Get account's SMS balance
     *
     * @return AccountBalanceResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public AccountBalanceResult getSMSBalance() throws APIConnectionException, APIRequestException {
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _accountPath + "/dev");
        return AccountBalanceResult.fromResponse(responseWrapper, AccountBalanceResult.class);
    }

    /**
     * Get app's SMS balance of an account
     *
     * @return AppBalanceResult
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public AppBalanceResult getAppSMSBalance() throws APIConnectionException, APIRequestException {
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _accountPath + "/app");
        return AppBalanceResult.fromResponse(responseWrapper, AppBalanceResult.class);
    }

    //===============      Template API     =================

    /**
     * Create template sms.
     *
     * @param payload {@link TemplatePayload }
     * @return {@link SendTempSMSResult }, include temp_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public SendTempSMSResult createTemplate(TemplatePayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Template payload should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _tempMsgPath, payload.toString());
        return SendTempSMSResult.fromResponse(responseWrapper, SendTempSMSResult.class);
    }

    /**
     * update template sms. Template can be modified ONLY when status is not approved
     *
     * @param payload {@link TemplatePayload }
     * @param tempId  template id
     * @return {@link SendTempSMSResult }, include temp_id
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public SendTempSMSResult updateTemplate(TemplatePayload payload, int tempId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "Template payload should not be null");
        Preconditions.checkArgument(tempId > 0, "temp id is invalid");
        ResponseWrapper responseWrapper = _httpClient.sendPut(_baseUrl + _tempMsgPath + "/" + tempId, payload.toString());
        return SendTempSMSResult.fromResponse(responseWrapper, SendTempSMSResult.class);
    }

    /**
     * check template by id
     *
     * @param tempId necessary
     * @return {@link TempSMSResult}
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public TempSMSResult checkTemplate(int tempId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(tempId > 0, "temp id is invalid");
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _tempMsgPath + "/" + tempId);
        return TempSMSResult.fromResponse(responseWrapper, TempSMSResult.class);
    }

    /**
     * Delete template by id
     *
     * @param tempId necessary
     * @return No content
     * @throws APIConnectionException connect exception
     * @throws APIRequestException    request exception
     */
    public ResponseWrapper deleteTemplate(int tempId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(tempId > 0, "temp id is invalid");
        return _httpClient.sendDelete(_baseUrl + _tempMsgPath + "/" + tempId);
    }

    public SignResult createSign(SignPayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "sign payload should not be null");
        Preconditions.checkArgument(payload.getType() > 0 && payload.getType() < 7,
                "type should be between 1 and 7");
        Preconditions.checkArgument(!StringUtils.isEmpty(payload.getSign()),
                "sign should not be null");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SignPayload.getSIGN(), payload.getSign());
        params.put(SignPayload.getREMARK(), payload.getRemark());
        params.put(SignPayload.getTYPE(), payload.getType());
        Map<String, byte[]> fileParams = new HashMap<String, byte[]>();
        for (File file : payload.getImages()) {
            fileParams.put(file.getName(), getBytes(file));
        }
        String url = _baseUrl + _signPath;
        try {
            return doPostSign(url, params, fileParams, SignPayload.getIMAGES());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("fail to creat sign");
        }
    }

    public SignResult updateSign(SignPayload payload, int signId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "sign payload should not be null");
        Preconditions.checkArgument(payload.getType() > 0 && payload.getType() < 7,
                "type should be between 1 and 7");
        Preconditions.checkArgument(!StringUtils.isEmpty(payload.getSign()),
                "sign should not be null");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SignPayload.getSIGN(), payload.getSign());
        params.put(SignPayload.getREMARK(), payload.getRemark());
        params.put(SignPayload.getTYPE(), payload.getType());
        Map<String, byte[]> fileParams = new HashMap<String, byte[]>();
        for (File file : payload.getImages()) {
            fileParams.put(file.getName(), getBytes(file));
        }
        String url = _baseUrl + _signPath + "/" + signId;
        try {
            return doPostSign(url, params, fileParams, SignPayload.getIMAGES());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("fail to update sign");
        }
    }

    public ResponseWrapper deleteSign(int signId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(signId > 0, "sign id is invalid");
        return _httpClient.sendDelete(_baseUrl + _signPath + "/" + signId);
    }

    public SignInfoResult checkSign(int signId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(signId > 0, "sign id is invalid");
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _tempMsgPath + "/" + signId);
        return SignInfoResult.fromResponse(responseWrapper, SignInfoResult.class);
    }

    public ResponseWrapper setDefaultSign(DefaultSignPayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(payload != null, "sign should not be null");
        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _signDefaultPath, payload.toString());
        return responseWrapper;
    }

    public SignResult doPostSign(String strUrl, Map<String, Object> params, Map<String, byte[]> fileParams,
                                 String imageName) throws Exception {
        URL url = new URL(strUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Authorization", _authCode);
        connection.setRequestProperty("Accept", "application/json, text/plain, */*"); // 设置接收数据的格式
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY); // 设置发送数据的格式
        connection.connect();
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            out.writeBytes("--" + BOUNDARY + NEWLINE);
            out.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"");
            out.writeBytes(NEWLINE + NEWLINE);
            out.writeBytes(value + NEWLINE);
        }
        if (fileParams != null && fileParams.size() > 0) {
            Iterator fileIt = fileParams.entrySet().iterator();
            while (fileIt.hasNext()) {
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIt.next();
                out.writeBytes("--" + BOUNDARY + NEWLINE);
                out.writeBytes("Content-Disposition: form-data; name=\"" + imageName
                        + "\"; filename=\"" + fileEntry.getKey() + "\"");
                out.writeBytes(NEWLINE);
                out.writeBytes("Content-Type: image/jpeg");//此处很关键
                out.writeBytes(NEWLINE + NEWLINE);
                out.write(fileEntry.getValue());
                out.writeBytes(NEWLINE);
            }
        }
        out.writeBytes("--" + BOUNDARY + "--");
        out.flush();
        out.close();
        InputStream in;
        int code = connection.getResponseCode();
        StringBuffer stringBuffer = new StringBuffer();
        try {
            if (code == 200) {
                in = connection.getInputStream();
            } else {
                in = connection.getErrorStream();
            }
        } catch (SSLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("fail to get inputStream");
        }
        if (null != in) {
            InputStreamReader responseContent = new InputStreamReader(in, "UTF-8");
            char[] quota = new char[1024];

            int remaining;
            while ((remaining = responseContent.read(quota)) > 0) {
                stringBuffer.append(quota, 0, remaining);
            }
        }
        ResponseWrapper wrapper = new ResponseWrapper();
        String responseContentStr = stringBuffer.toString();
        wrapper.responseCode = code;
        wrapper.responseContent = responseContentStr;
        String quota1 = connection.getHeaderField("X-Rate-Limit-Limit");
        String remaining1 = connection.getHeaderField("X-Rate-Limit-Remaining");
        String reset = connection.getHeaderField("X-Rate-Limit-Reset");
        wrapper.setRateLimit(quota1, remaining1, reset);

        if (code >= 200 && code < 300) {
            LOG.debug("Succeed to get response OK - responseCode:" + code);
            LOG.debug("Response Content - " + responseContentStr);
        } else {
            if (code < 300 || code >= 400) {
                LOG.warn("Got error response - responseCode:" + code + ", responseContent:" + responseContentStr);
                wrapper.setErrorObject();
                throw new APIRequestException(wrapper);
            }

            LOG.warn("Normal response but unexpected - responseCode:" + code + ", responseContent:" + responseContentStr);
        }
        return SignResult.fromResponse(wrapper, SignResult.class);
    }

    public static byte[] getBytes(File f) {
        try {
            InputStream in = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1)
                out.write(b, 0, n);
            in.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("File is invalid, please check again");
        }
    }

}
