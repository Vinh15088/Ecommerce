package com.LaptopWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Integer id;

    String orderCode;

    String fullName;
    String address;
    String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.XXX", timezone = "Asia/Ho_Chi_Minh")
    Date createdAt;

    Long totalPrice;

    String note;

    boolean paymentStatus;
    String paymentType;

    List<OrderDetailResponse> orderDetailResponses;

    String orderStatus;
}
