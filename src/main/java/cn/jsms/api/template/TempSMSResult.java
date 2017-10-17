package cn.jsms.api.template;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class TempSMSResult extends BaseResult {

    @Expose int temp_id;
    @Expose String template;
    @Expose int type;
    @Expose int ttl;
    @Expose String remark;
    @Expose int status;

    public int getTempId() {
        return temp_id;
    }

    public String getTemplate() {
        return template;
    }

    public int getType() {
        return type;
    }

    public int getTtl() {
        return ttl;
    }

    public String getRemark() {
        return remark;
    }

    public int getStatus() {
        return status;
    }
}
