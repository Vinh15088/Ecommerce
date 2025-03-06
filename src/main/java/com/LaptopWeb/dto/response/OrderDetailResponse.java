package com.LaptopWeb.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    Integer id;

    int unitPrice;
    int quantity;
    int totalPrice;

    String product;
}
