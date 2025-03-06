package com.LaptopWeb.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailRequest {

    int unitPrice;
    int quantity;
    int totalPrice;

    Integer productId;
}
