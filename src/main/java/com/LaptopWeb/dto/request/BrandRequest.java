package com.LaptopWeb.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandRequest {

    @NotEmpty(message = "BRAND_NAME_EMPTY")
    String name;

}
