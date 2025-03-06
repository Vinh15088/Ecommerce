package com.LaptopWeb.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

    @NotEmpty(message = "CATEGORY_NAME_EMPTY")
    String name;

    String description;

    boolean enabled;

    Integer parent_id;
}
