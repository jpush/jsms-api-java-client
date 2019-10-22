package cn.jsms.api.sign;

import cn.jiguang.common.resp.BaseResult;
import com.google.gson.annotations.Expose;

public class SignResult extends BaseResult {
    @Expose
    int sign_id;

    public int getSign_id() {
        return sign_id;
    }
}
