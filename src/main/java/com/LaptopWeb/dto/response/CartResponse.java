package com.LaptopWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Integer id;

    JsonNode user;

    List<JsonNode> cartItemResponses;

    JsonNode cartSummary;
}
