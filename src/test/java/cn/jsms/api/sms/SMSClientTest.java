package cn.jsms.api.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.BaseTest;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.SlowTests;
import cn.jsms.api.ValidSMSResult;
import cn.jsms.api.common.JSMSConfig;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;


@Category(SlowTests.class)
public class SMSClientTest extends BaseTest {
	
	private static Logger LOG = LoggerFactory.getLogger(SMSClientTest.class);
	private SMSClient client = null;
	
	@Before
	public void before() throws Exception {
		client = new SMSClient(MASTER_SECRET, APP_KEY, null, JSMSConfig.getInstance());
	}
	
	@Test
	public void testSendSMSCode() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("13800138000")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "13800138000");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
		
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
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_MobileNull() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_MobileEmpty() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
	}
	
	/**
	 * mobile number less than 11
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_InvalidMobile1() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("1382")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "1382");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
	}
	
	/**
	 * mobile number more than 11
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_InvalidMobile2() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("138000000000")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "138000000000");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
	}
	
	/**
	 * invalid mobile number
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_InvalidMobile3() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("12345678901")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "12345678901");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_TempIdNull() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("13800138000")
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "13800138000");
		
		assertEquals(payload.toJSON(), json);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_TempIdNegative() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobildNumber("13800138000")
				.setTempId(-1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "13800138000");
		json.addProperty("temp_id", -1);
		
		assertEquals(payload.toJSON(), json);
	}
	
	@Test
	public void testSendValidSMSCode() {
		try {
			ValidSMSResult res = client.sendValidSMSCode("f3247cce-811c-4260-9bc6-ed27e2e81963", 865425);
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
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendValidSMSCode_InvalidCode1() {
		try {
			client.sendValidSMSCode("5d7f4f78-5f41-4025-a253-50bc9a3ae1d6", -1);
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
        	e.printStackTrace();
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendValidSMSCode_InvalidCode2() {
		try {
			client.sendValidSMSCode("5d7f4f78-5f41-4025-a253-50bc9a3ae1d6", 123);
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
        	e.printStackTrace();
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}
	
	@Test
	public void testSendValidSMSCode_InvalidCode3() {
		try {
			client.sendValidSMSCode("5d7f4f78-5f41-4025-a253-50bc9a3ae1d6", 1234567);
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
