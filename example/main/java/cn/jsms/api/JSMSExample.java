package cn.jsms.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.common.JSMSConfig;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;


public class JSMSExample {

	protected static final Logger LOG = LoggerFactory.getLogger(JSMSExample.class);

    private static final String appkey = "242780bfdd7315dc1989fe2b";
    private static final String masterSecret = "2f5ced2bef64167950e63d13";
    
    public static void main(String[] args) {
//    	testSendSMSCode();
//    	testSendValidSMSCode();
    }
    
    public static void testSendSMSCode() {
    	SMSClient client = new SMSClient(masterSecret, appkey, null, JSMSConfig.getInstance());
    	SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("13800138000")
				.setTempId(1)
				.build();
    	try {
			SendSMSResult res = client.sendSMSCode(payload);
			assertTrue(res.isResultOK());
			System.out.println(res.toString());
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
        	e.printStackTrace();
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
    
    public static void testSendValidSMSCode() {
    	SMSClient client = new SMSClient(masterSecret, appkey, null, JSMSConfig.getInstance());
		try {
			ValidSMSResult res = client.sendValidSMSCode("23956732-d63f-438b-b940-e1578cc0199f", 225415);
			assertEquals(true, res.getIsValid());
			System.out.println(res.toString());
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
        	e.printStackTrace();
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}
    
}
