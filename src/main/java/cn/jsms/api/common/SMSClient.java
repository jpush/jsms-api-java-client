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
    private final String BOUNDARY = "========7d4a6d158c9";

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

    /**
     * create sign
     *
     * @param payload
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public SignResult createSign(SignPayload payload) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "sign payload should not be null");
        Preconditions.checkArgument(payload.getType() > 0 && payload.getType() <= 7,
                "type should be between 1 and 7");
        Preconditions.checkArgument(!StringUtils.isEmpty(payload.getSign()),
                "sign should not be null");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SignPayload.getSIGN(), payload.getSign());
        params.put(SignPayload.getREMARK(), payload.getRemark());
        params.put(SignPayload.getTYPE(), payload.getType());
        Map<String, byte[]> fileParams = new HashMap<String, byte[]>();
        File[] images = payload.getImages();
        if (images != null && images.length > 0) {
            for (File file : payload.getImages()) {
                fileParams.put(file.getName(), getBytes(file));
            }
        }
        String url = _baseUrl + _signPath;
        try {
            ResponseWrapper wrapper = doPostSign(url, params, fileParams, SignPayload.getIMAGES());
            return SignResult.fromResponse(wrapper, SignResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("fail to creat sign");
        }
    }

    /**
     * update sign
     *
     * @param payload
     * @param signId
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public SignResult updateSign(SignPayload payload, int signId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(null != payload, "sign payload should not be null");
        Preconditions.checkArgument(payload.getType() > 0 && payload.getType() <= 7,
                "type should be between 1 and 7");
        Preconditions.checkArgument(!StringUtils.isEmpty(payload.getSign()),
                "sign should not be null");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SignPayload.getSIGN(), payload.getSign());
        params.put(SignPayload.getREMARK(), payload.getRemark());
        params.put(SignPayload.getTYPE(), payload.getType());
        Map<String, byte[]> fileParams = new HashMap<String, byte[]>();
        File[] images = payload.getImages();
        if (images != null && images.length > 0) {
            for (File file : payload.getImages()) {
                fileParams.put(file.getName(), getBytes(file));
            }
        }
        String url = _baseUrl + _signPath + "/" + signId;
        try {
            ResponseWrapper wrapper = doPostSign(url, params, fileParams, SignPayload.getIMAGES());
            return SignResult.fromResponse(wrapper, SignResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("fail to update sign");
        }
    }

    /**
     * delete sig by id
     *
     * @param signId
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public ResponseWrapper deleteSign(int signId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(signId > 0, "sign id is invalid");
        return _httpClient.sendDelete(_baseUrl + _signPath + "/" + signId);
    }

    /**
     * get sign by id
     *
     * @param signId
     * @return
     * @throws APIConnectionException
     * @throws APIRequestException
     */
    public SignInfoResult checkSign(int signId) throws APIConnectionException, APIRequestException {
        Preconditions.checkArgument(signId > 0, "sign id is invalid");
        ResponseWrapper responseWrapper = _httpClient.sendGet(_baseUrl + _signPath + "/" + signId);
        return SignInfoResult.fromResponse(responseWrapper, SignInfoResult.class);
    }

//    /**
//     * set default sign  Discard
//     *
//     * @param payload
//     * @return
//     * @throws APIConnectionException
//     * @throws APIRequestException
//     */
//    public ResponseWrapper setDefaultSign(DefaultSignPayload payload) throws APIConnectionException, APIRequestException {
//        Preconditions.checkArgument(payload != null, "sign should not be null");
//        ResponseWrapper responseWrapper = _httpClient.sendPost(_baseUrl + _signDefaultPath, payload.toString());
//        return responseWrapper;
//    }

    /**
     * post sign
     * @param strUrl
     * @param params
     * @param fileParams
     * @param fileName
     * @return
     * @throws Exception
     */
    public ResponseWrapper doPostSign(String strUrl, Map<String, Object> params, Map<String,
            byte[]> fileParams, String fileName) throws Exception {
        ResponseWrapper wrapper = new ResponseWrapper();
        String TWO_HYPHENS = "--";
        String LINE_END = "\r\n";

        URL url = new URL(strUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();//得到connection对象
        /************************************设置请求头*************************************************/
        connection.setRequestMethod("POST");    //设置请求方式为POST
        connection.setDoOutput(true);           //允许写出
        connection.setDoInput(true);            //允许读入
        connection.setUseCaches(false);         //不使用缓存
        connection.setRequestProperty("Charset", "utf-8");//编码格式
        connection.setRequestProperty("Authorization", _authCode);
        connection.setRequestProperty("Content-Type", "multipart/form-data ; boundary=" + BOUNDARY); // 设置发送数据的格式(form-data格式)   //boundary为头部分隔符，头部拼接时需要分隔符。例如下面的有多个"Content-Disposition"拼接时需要用到此分隔符
        connection.connect(); //连接
        /************************************输出流，写数据,start*************************************************/
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());//获得输出流对象
        StringBuffer strBufparam = new StringBuffer();
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            //封装键值对数据
            Map.Entry<String, String> entry = (Map.Entry) it.next();
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            strBufparam.append(TWO_HYPHENS);
            strBufparam.append(BOUNDARY);
            strBufparam.append(LINE_END);//"--" + BOUNDARY + "\r\n"
            strBufparam.append("Content-Disposition: form-data; name=\"" + key + "\"");
            strBufparam.append(LINE_END);
            strBufparam.append(LINE_END);
            strBufparam.append(value);
            strBufparam.append(LINE_END);
        }
        out.write(strBufparam.toString().getBytes("utf-8"));
        //写入图片参数
        if (fileParams != null && fileParams.size() > 0) {
            Iterator fileIt = fileParams.entrySet().iterator();
            while (fileIt.hasNext()) {
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIt.next();
                //拼接文件的参数
                StringBuffer strBufFile = new StringBuffer();
                strBufFile.append(TWO_HYPHENS);
                strBufFile.append(BOUNDARY);
                strBufFile.append(LINE_END);
                strBufFile.append("Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"" + fileEntry.getKey() + "\"");// filename：参数名。fileEntry.getKey()：文件名称
                strBufFile.append(LINE_END);
                strBufFile.append("Content-Type:application/octet-stream");
                strBufFile.append(LINE_END);
                strBufFile.append(LINE_END);
                out.write(strBufFile.toString().getBytes("utf-8"));
                out.write(fileEntry.getValue());//文件 (此参数之前调用了本页面的重写方法getBytes(File f)，将文件转换为字节数组了 )
                out.write((LINE_END).getBytes());
            }
        }

        //写入标记结束位
        byte[] endData = (TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END).getBytes();//写结束标记位
        out.write(endData);
        out.flush();
        out.close();

        /************************************输出流，写数据完成end*************************************************/
        int code = connection.getResponseCode(); //获得响应码（200为成功返回）
        InputStream in;
        try {
            if (code == HttpURLConnection.HTTP_OK) {
                in = connection.getInputStream(); //获取响应流
            } else {
                in = connection.getErrorStream(); //获取响应流
            }
        } catch (SSLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("fail to get inputStream");
        }
        /**********读取返回的输入流信息**************/
        byte[] bytes;
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        if (in != null){
            int len;
            while ((len = in.read(buff)) != -1) {
                baout.write(buff, 0, len);
            }
        }
        bytes = baout.toByteArray();
        in.close();
        String ret = new String(bytes, "utf-8");
        /**********封装返回的输入流信息**************/
        String responseContentStr = ret;
        wrapper.responseCode = code;
        wrapper.responseContent = responseContentStr;
        return wrapper;
    }

    /**
     * 将文件转换为byte数组
     *
     * @param f
     * @return
     */
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
            e.printStackTrace();
            throw new IllegalArgumentException("File is invalid, please check again");
        }
    }


}
