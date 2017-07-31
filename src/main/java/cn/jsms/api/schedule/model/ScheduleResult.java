package cn.jsms.api.schedule.model;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class ScheduleResult extends BaseResult {

    @Expose String schedule_id;

    public String getScheduleId() {
        return schedule_id;
    }
}
