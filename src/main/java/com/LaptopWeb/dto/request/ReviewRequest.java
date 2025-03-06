package com.LaptopWeb.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    @NotBlank(message = "REVIEW_COMMENT_BLANK")
    String comment;

    @NotNull(message = "REVIEW_RATING_NULL")
    Integer rating;

    Integer product_id;
}
