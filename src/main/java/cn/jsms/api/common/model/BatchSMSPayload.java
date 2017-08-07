package cn.jsms.api.common.model;

import cn.jiguang.common.utils.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BatchSMSPayload implements IModel {

    private static String TEMP_ID = "temp_id";
    private static String RECIPIENTS = "recipients";

    private int temp_id;
    private static Gson gson = new Gson();
    private JsonArray recipients;

    public BatchSMSPayload(int tempId, JsonArray recipients) {
        this.temp_id = tempId;
        this.recipients = recipients;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private int temp_id;
        private JsonArray recipients = new JsonArray();

        public Builder setTempId(int tempId) {
            this.temp_id = tempId;
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
            return new BatchSMSPayload(temp_id, recipients);
        }
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();
        if (temp_id > 0) {
            jsonObject.addProperty(TEMP_ID, temp_id);
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
