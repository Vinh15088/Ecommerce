package com.LaptopWeb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    @NotBlank(message = "ORDER_ADDRESS_NOT_BLANK")
    String address;

    @NotBlank(message = "ORDER_PHONE_NOT_BLANK")
    String phone;

    String note;

    @NotBlank(message = "ORDER_PAYMENT_TYPE_NOT_BLANK")
    String paymentType;

    @NotEmpty(message = "ORDER_DETAIL_NOT_EMPTY")
    List<OrderDetailRequest> detailRequests;

    Long totalPrice;
}
