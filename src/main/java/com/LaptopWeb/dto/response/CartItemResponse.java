package com.LaptopWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Integer id;
    int quantity;
    Long price;

    JsonNode product;
}
