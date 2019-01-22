package cn.jsms.api.common.model;

import cn.jiguang.common.utils.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BatchSMSPayload implements IModel {

    private static String SIGN_ID = "sign_id";
    private static String TEMP_ID = "temp_id";
    private static String TAG = "tag";
    private static String RECIPIENTS = "recipients";

    private int sign_id;
    private int temp_id;
    private String tag;
    private static Gson gson = new Gson();
    private JsonArray recipients;

    private BatchSMSPayload(int signId, String tag, int tempId, JsonArray recipients) {
        this.sign_id = signId;
        this.temp_id = tempId;
        this.tag = tag;
        this.recipients = recipients;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int sign_id;
        private int temp_id;
        private String tag;
        private JsonArray recipients = new JsonArray();

        public Builder setSignId(int signId) {
            this.sign_id = signId;
            return this;
        }

        public Builder setTempId(int tempId) {
            this.temp_id = tempId;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setRecipients(RecipientPayload...recipients) {
            if (recipients == null) {
                return this;
            }

            for (RecipientPayload recipientPayload : recipients) {
                this.recipients.add(recipientPayload.toJSON());
            }
            return this;
        }

        public Builder addRecipient(RecipientPayload recipientPayload) {
            Preconditions.checkArgument(null != recipientPayload, "RecipientPayload should not be null");
            this.recipients.add(recipientPayload.toJSON());
            return this;
        }

        public BatchSMSPayload build() {
            Preconditions.checkArgument(temp_id >= 0, "temp id should not less 0");
            return new BatchSMSPayload(sign_id, tag, temp_id, recipients);
        }
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

        if (sign_id > 0) {
            jsonObject.addProperty(SIGN_ID, sign_id);
        }

        if (temp_id > 0) {
            jsonObject.addProperty(TEMP_ID, temp_id);
        }

        if (null != tag) {
            jsonObject.addProperty(TAG, tag);
        }

        if (null != recipients && recipients.size() > 0) {
            jsonObject.add(RECIPIENTS, recipients);
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return gson.toJson(toJSON());
    }
}
