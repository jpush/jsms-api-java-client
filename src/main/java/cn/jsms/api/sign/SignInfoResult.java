package cn.jsms.api.sign;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class SignInfoResult extends BaseResult {
    @Expose
    int sign_id;
    @Expose
    String sign;
    @Expose
    int status;
    @Expose
    int is_default;

    public int getSign_id() {
        return sign_id;
    }

    public String getSign() {
        return sign;
    }

    public int getStatus() {
        return status;
    }

    public int getIs_default() {
        return is_default;
    }
}
