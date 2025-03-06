package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.ReviewRequest;
import com.LaptopWeb.dto.response.ReviewResponse;
import com.LaptopWeb.entity.Review;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toReview(ReviewRequest request);

    @Mapping(target = "user", expression = "java(buildUser(review))")
    @Mapping(target = "product", expression = "java(buildProduct(review))")
    ReviewResponse toReviewResponse(Review review);

    default JsonNode buildUser(Review review){
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("id", review.getUser().getId());
        objectNode.put("username", review.getUser().getUsername());

        return objectNode;
    }

    default JsonNode buildProduct(Review review) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("id", review.getProduct().getId());
        objectNode.put("name", review.getProduct().getName());

        return objectNode;
    }
}
