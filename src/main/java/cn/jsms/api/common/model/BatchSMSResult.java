package cn.jsms.api.common.model;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.util.List;

public class BatchSMSResult extends BaseResult {

    @Expose String schedule_id;
    @Expose int success_count;
    @Expose int failure_count;
    @Expose List<FailureRecipients> failure_recipients;

    public String getScheduleId() {
        return schedule_id;
    }

    public int getSuccessCount() {
        return success_count;
    }

    public int getFailureCount() {
        return failure_count;
    }

    public List<FailureRecipients> getFailureRecipients() {
        return failure_recipients;
    }

    public static class FailureRecipients {

        @Expose String error_code;
        @Expose String error_message;
        @Expose String mobile;
        @Expose JsonObject temp_para;

        public String getErrorCode() {
            return error_code;
        }

        public String getErrorMessage() {
            return error_message;
        }

        public String getMobile() {
            return mobile;
        }

        public JsonObject getTempPara() {
            return temp_para;
        }
    }
}
