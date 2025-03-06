package com.LaptopWeb.dto.request;

import com.LaptopWeb.entity.CartItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {

    List<CartItemRequest> cartItemRequests;
}
