package com.LaptopWeb.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {

//    @Size(min = 1, message = "CART_ITEM_QUANTITY_MIN")
    @Min(value = 1, message = "CART_ITEM_QUANTITY_MIN")
    int quantity;

    Integer productId;
}
