package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.CartItemRequest;
import com.LaptopWeb.dto.response.CartItemResponse;
import com.LaptopWeb.entity.CartItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "product", ignore = true)
    CartItem toCartItem(CartItemRequest request);

    @Mapping(target = "product", expression = "java(buildProduct(cartItem))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    default JsonNode buildProduct(CartItem cartItem) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();

        node.put("productId", cartItem.getProduct().getId());
        node.put("images", cartItem.getProduct().getImages().toString());
        node.put("name", cartItem.getProduct().getName());
        node.put("unitPrice", cartItem.getProduct().getPrice());
        node.put("quantity", cartItem.getQuantity());
        node.put("unitCost", cartItem.getProduct().getCost());
        node.put("discount", cartItem.getProduct().getDiscount());
        node.put("totalPrice", cartItem.getPrice());

        return node;
    }
}
