package cn.jsms.api.sign;

import cn.jiguang.common.utils.Preconditions;
import cn.jiguang.common.utils.StringUtils;

import java.io.File;

public class SignPayload {
    private String sign;
    private File[] images;
    private String remark;
    private Integer type;

    private SignPayload(Builder builder) {
        sign = builder.sign;
        images = builder.images;
        remark = builder.remark;
        type = builder.type;
    }

    public static final class Builder {
        private String sign;
        private File[] images;
        private String remark;
        private Integer type;

        public Builder() {
        }

        public Builder sign(String val) {
            sign = val;
            return this;
        }

        public Builder images(File[] val) {
            images = val;
            return this;
        }

        public Builder remark(String val) {
            remark = val;
            return this;
        }

        public Builder type(Integer val) {
            type = val;
            return this;
        }

        public SignPayload build() {
            Preconditions.checkArgument(!StringUtils.isEmpty(sign), "sign should be not empty");
            Preconditions.checkArgument(type > 0 && type < 7, "type should be between 1 and 7");
            return new SignPayload(this);
        }
    }
}
