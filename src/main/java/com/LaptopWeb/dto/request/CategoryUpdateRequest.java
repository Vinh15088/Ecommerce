package com.LaptopWeb.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryUpdateRequest {

    String name;

    String description;

    boolean enabled;

    Integer parent_id;
}
