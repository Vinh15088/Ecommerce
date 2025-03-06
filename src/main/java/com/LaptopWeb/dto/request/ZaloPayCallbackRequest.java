package com.LaptopWeb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZaloPayCallbackRequest {
    String data;
    String mac;
    int type;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ZaloPayCallbackData {

        @JsonProperty("app_id")
        int appId;

        @JsonProperty("app_trans_id")
        String appTransactionId;

        @JsonProperty("app_time")
        long appTime;

        @JsonProperty("app_user")
        String appUser;

        @JsonProperty("amount")
        long amount;

        JsonNode embedData;

        JsonNode item;

        @JsonProperty("zp_trans_id")
        long zpTransactionId;

        @JsonProperty("server_time")
        long serverTime;

        @JsonProperty("channel")
        int channel;

        @JsonProperty("merchant_user_id")
        String merchantUserId;

        @JsonProperty("zp_user_id")
        String zpUserId;

        @JsonProperty("user_fee_amount")
        long userFeeAmount;

        @JsonProperty("discount_amount")
        long discountAmount;

        @JsonProperty("embed_data")
        public void setEmbed_data(String embed_data) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
//            this.embed_data = mapper.convertValue(embed_data, JsonNode.class);
            this.embedData = mapper.readTree(embed_data);
        }

        @JsonProperty("item")
        public void setItem(String item) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            this.item = mapper.readTree(item);
        }
    }
}
