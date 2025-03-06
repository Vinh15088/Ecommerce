package com.LaptopWeb.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {

    @NotBlank(message = "PRODUCT_NAME_NOT_BLANK")
    String name;

    @NotBlank(message = "PRODUCT_SKU_NOT_BLANK")
    String sku;

    String shortDescription;
    String longDescription;


    @Min(value = 0, message = "PRODUCT_STOCK_MIN_0")
    @NotNull(message = "PRODUCT_STOCK_NOT_NULL")
    Integer stock;

    Long cost;
    Long price;
    Integer discount;

    boolean active;

    Map<String, Object> productDetails;

    Integer category_id;
    Integer brand_id;

    List<MultipartFile> newImages;
    List<String> imagesToDelete;
}
