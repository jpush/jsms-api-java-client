package cn.jsms.api.schedule.model;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.List;

public class ScheduleSMSResult extends BaseResult {

    @Expose String schedule_id;
    @Expose String send_time;
    @Expose int temp_id;
    @Expose List<Recipient> recipients;

    public String getScheduleId() {
        return schedule_id;
    }

    public String getSendTime() {
        return send_time;
    }

    public int getTempId() {
        return temp_id;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public static class Recipient {
        @Expose String msg_id;
        @Expose String mobile;
        @Expose JsonObject temp_para;

        public String getMsgId() {
            return msg_id;
        }

        public String getMobile() {
            return mobile;
        }

        public JsonObject getTempPara() {
            return temp_para;
        }
    }
}
