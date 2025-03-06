package com.LaptopWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Integer id;
    String comment;
    Integer rating;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.XXX", timezone = "Asia/Ho_Chi_Minh")
    Date createdAt;

    JsonNode user;
    JsonNode product;
}
