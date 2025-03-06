package com.LaptopWeb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Integer id;

    String name;

    String description;

    boolean enabled;

    Integer parent_id;

    List<CategoryResponse> children;
}
