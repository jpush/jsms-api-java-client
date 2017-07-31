package cn.jsms.api.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cn.jiguang.common.resp.ResponseWrapper;
import cn.jsms.api.account.AccountBalanceResult;
import cn.jsms.api.account.AppBalanceResult;
import cn.jsms.api.schedule.model.RecipientPayload;
import cn.jsms.api.schedule.model.ScheduleListResult;
import cn.jsms.api.schedule.model.ScheduleResult;
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
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Category(SlowTests.class)
public class SMSClientTest extends BaseTest {
	
	private static Logger LOG = LoggerFactory.getLogger(SMSClientTest.class);
	private SMSClient client = null;
	
	@Before
	public void before() throws Exception {
		client = new SMSClient(MASTER_SECRET, APP_KEY);
	}
	
	@Test
	public void testSendSMSCode() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobileNumber("13800138000")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "13800138000");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
		
		try {
			SendSMSResult res = client.sendSMSCode(payload);
			assertTrue(res.isResultOK());
			LOG.info(res.toString());
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
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
				.setMobileNumber("")
				.setTempId(1)
				.build();
		
		JsonObject json = new JsonObject();
		json.addProperty("mobile", "");
		json.addProperty("temp_id", 1);
		
		assertEquals(payload.toJSON(), json);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendSMSCode_TempIdNegative() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobileNumber("13800138000")
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
			ValidSMSResult res = client.sendValidSMSCode("f3247cce-811c-4260-9bc6-ed27e2e81963", "865425");
			assertEquals(true, res.getIsValid());
			LOG.info(res.toString());
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendValidSMSCode_InvalidCode1() {
		try {
			client.sendValidSMSCode("5d7f4f78-5f41-4025-a253-50bc9a3ae1d6", "-1");
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendValidSMSCode_InvalidCode2() {
		try {
			client.sendValidSMSCode("5d7f4f78-5f41-4025-a253-50bc9a3ae1d6", "123");
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSendValidSMSCode_InvalidCode3() {
		try {
			client.sendValidSMSCode("5d7f4f78-5f41-4025-a253-50bc9a3ae1d6", "1234567");
		} catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}

	@Test
    public void testSendVoiceSMSCode() {
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber("13800138000")
                .build();

        JsonObject json = new JsonObject();
        json.addProperty("mobile", "13800138000");
        assertEquals(payload.toJSON(), json);

        try {
            SendSMSResult res = client.sendVoiceSMSCode(payload);
            LOG.info(res.toString());
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        }
    }

	@Test
	public void testSendTempSMS() {
		SMSPayload payload = SMSPayload.newBuilder()
				.setMobileNumber("13800138000")
				.setTempId(5118)
				.addTempPara("test", "jpush")
				.build();

		JsonObject json = new JsonObject();
		json.addProperty("mobile", "13800138000");
		json.addProperty("temp_id", 5118);
		JsonObject tempJson = new JsonObject();
		tempJson.addProperty("test", "jpush");
		json.add("temp_para", tempJson);
		assertEquals(payload.toJSON(), json);

		try {
			SendSMSResult res = client.sendTemplateSMS(payload);
			assertTrue(res.isResultOK());
			LOG.info(res.toString());
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Message: " + e.getMessage());
		}
	}

    @Test
    public void testSendTempSMS_withMap() {
        Map<String, String> test = new HashMap<String, String>();
        test.put("test", "jpush");
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber("13800138000")
                .setTempId(5118)
                .setTempPara(test)
                .build();

        JsonObject json = new JsonObject();
        json.addProperty("mobile", "13800138000");
        json.addProperty("temp_id", 5118);
        JsonObject tempJson = new JsonObject();
        tempJson.addProperty("test", "jpush");
        json.add("temp_para", tempJson);
        assertEquals(payload.toJSON(), json);

        try {
            SendSMSResult res = client.sendTemplateSMS(payload);
            assertTrue(res.isResultOK());
            LOG.info(res.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

	@Test
	public void testSendTempSMS_tempParaNull() {
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber("13800138000")
                .setTempId(5118)
                .build();
        try {
            SendSMSResult res = client.sendTemplateSMS(payload);
            assertTrue(res.getResponseCode() == 403);
            LOG.info(res.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
	}

	@Test
    public void testSendScheduleSMS() {
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber("13570210796")
                .setTempId(1111)
                .setSendTime("2017-07-31 16:17:00")
                .addTempPara("number", "798560")
                .build();
        try {
            ScheduleResult result = client.sendScheduleSMS(payload);
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateScheduleSMS() {
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber("13800138000")
                .setTempId(1111)
                .setSendTime("2017-07-31 15:00:00")
                .addTempPara("number", "110110")
                .build();
        try {
            ScheduleResult result = client.updateScheduleSMS(payload, "dsfd");
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    @Test
    public void testSendBatchScheduleSMS() {
        List<RecipientPayload> list = new ArrayList<RecipientPayload>();
        RecipientPayload recipientPayload1 = new RecipientPayload.Builder()
                .setMobile("13800138000")
                .addTempPara("number", "638938")
                .build();
        RecipientPayload recipientPayload2 = new RecipientPayload.Builder()
                .setMobile("13800138001")
                .addTempPara("number", "829302")
                .build();
        list.add(recipientPayload1);
        list.add(recipientPayload2);
        RecipientPayload[] recipientPayloads = new RecipientPayload[list.size()];
        SMSPayload smsPayload = SMSPayload.newBuilder()
                .setSendTime("2017-07-31 16:00:00")
                .setTempId(1245)
                .setRecipients(recipientPayloads)
                .build();
        try {
            ScheduleListResult result = client.sendBatchScheduleSMS(smsPayload);
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateBatchScheduleSMS() {
        List<RecipientPayload> list = new ArrayList<RecipientPayload>();
        RecipientPayload recipientPayload1 = new RecipientPayload.Builder()
                .setMobile("13800138000")
                .addTempPara("number", "328393")
                .build();
        RecipientPayload recipientPayload2 = new RecipientPayload.Builder()
                .setMobile("13800138001")
                .addTempPara("number", "489042")
                .build();
        list.add(recipientPayload1);
        list.add(recipientPayload2);
        RecipientPayload[] recipientPayloads = new RecipientPayload[list.size()];
        SMSPayload smsPayload = SMSPayload.newBuilder()
                .setSendTime("2017-07-31 16:00:00")
                .setTempId(1245)
                .setRecipients(recipientPayloads)
                .build();
        try {
            ScheduleListResult result = client.updateBatchScheduleSMS(smsPayload, "dfs");
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteScheduleSMS() {
        try {
            ResponseWrapper result = client.deleteScheduleSMS("sd");
            LOG.info("Response content: " + result.responseContent + " response code: " + result.responseCode);
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    @Test
    public void testGetAccountSMSBalance() {
        try {
            AccountBalanceResult result = client.getSMSBalance();
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }

    @Test
    public void testGetAppSMSBalance() {
        try {
            AppBalanceResult result = client.getAppSMSBalance();
            LOG.info(result.toString());
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Message: " + e.getMessage());
        }
    }
}
