package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.CartRequest;
import com.LaptopWeb.dto.response.CartItemResponse;
import com.LaptopWeb.dto.response.CartResponse;
import com.LaptopWeb.entity.Cart;
import com.LaptopWeb.entity.CartItem;
import com.LaptopWeb.entity.ProductImage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    @Mapping(target = "user", expression = "java(buildUser(cart))")
    @Mapping(target = "cartItemResponses", expression = "java(buildCartItemResponses(cart))")
    @Mapping(target = "cartSummary", expression = "java(buildCartSummary(cart))")
    CartResponse toCartResponse(Cart cart);


    default JsonNode buildUser(Cart cart) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode user = mapper.createObjectNode();

        user.put("userId", cart.getUser().getId());
        user.put("username", cart.getUser().getUsername());

        return user;
    }

    default List<JsonNode> buildCartItemResponses(Cart cart) {
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> cartItemResponses = new ArrayList<>();

        String baseUrl = "https://ecommerce-vinhseo.s3.ap-southeast-2.amazonaws.com/products/";

        List<CartItem> cartItems = cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            ObjectNode node = mapper.createObjectNode();

            node.put("productId", cartItem.getProduct().getId());
            node.put("name", cartItem.getProduct().getName());

            List<String> imageUrls = cartItem.getProduct().getImages().stream()
                    .map(image ->baseUrl + image.getProduct().getId() + "/" + image.getUrl())
                    .collect(Collectors.toList());
            node.put("images", mapper.valueToTree(imageUrls));

            node.put("unitPrice", cartItem.getProduct().getPrice());
            node.put("quantity", cartItem.getQuantity());
            node.put("unitCost", cartItem.getProduct().getCost());
            node.put("discount", cartItem.getProduct().getDiscount());
            node.put("totalPrice", cartItem.getPrice());

            cartItemResponses.add(node);
        }

        return cartItemResponses;
    }

    default JsonNode buildCartSummary(Cart cart) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode summary = mapper.createObjectNode();

        Long totalPrice = cart.getCartItems().stream().mapToLong(CartItem::getPrice).sum();

        summary.put("totalItems", cart.getCartItems().size());
        summary.put("totalPrice", totalPrice);

        return summary;
    }

}
