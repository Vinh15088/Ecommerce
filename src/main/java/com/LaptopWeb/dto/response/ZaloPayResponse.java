package com.LaptopWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZaloPayResponse {
    @JsonProperty("return_code")
    private int returnCode;

    @JsonProperty("return_message")
    private String returnMessage;

    @JsonProperty("sub_return_code")
    private int subReturnCode;

    @JsonProperty("sub_return_message")
    private String subReturnMessage;

    @JsonProperty("zp_trans_token")
    private String zpTransToken;

    @JsonProperty("order_url")
    private String orderUrl;

    @JsonProperty("order_token")
    private String orderToken;
}
