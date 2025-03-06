package com.LaptopWeb.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Integer id;
    String name;
    String sku;
    String shortDescription;
    String longDescription;

    Integer stock;
    boolean active;

    Long cost;
    Long price;
    Integer discount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.XXX", timezone = "Asia/Ho_Chi_Minh")
    Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.XXX", timezone = "Asia/Ho_Chi_Minh")
    Date updatedAt;

    Double rating;

    List<String> images;

    JsonNode productDetails;

    String category;
    String brand;
}
