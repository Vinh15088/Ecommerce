package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.ProductRequest;
import com.LaptopWeb.dto.response.ProductResponse;
import com.LaptopWeb.entity.Product;
import com.LaptopWeb.entity.ProductImage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
//    @Mapping(target = "imagesToDelete", ignore = true)
    @Mapping(target = "productDetails", expression = "java(buildJsonToString(request))")
//    @Mapping(target = "images", expression = "java(mapImages(request.getNewImages(), product))")
    Product toProduct(ProductRequest request);

    @Mapping(target = "productDetails", expression = "java(buildStringToJson(product))")
    @Mapping(target = "category", expression = "java(buildCategoryName(product))")
    @Mapping(target = "brand", expression = "java(buildBrandName(product))")
//    @Mapping(target = "image", expression = "java(buildImageName(product))")
    @Mapping(target = "images", expression = "java(buildImageUrls(product))")
    ProductResponse toProductResponse(Product product);

    default String buildJsonToString(ProductRequest request) {
        try {
            Map<String, Object> productDetail = request.getProductDetails();

            return new ObjectMapper().writeValueAsString(productDetail);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    default JsonNode buildStringToJson(Product product) {
        try {
            String productDetail = product.getProductDetails();

            return new ObjectMapper().readTree(productDetail);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    default String buildCategoryName(Product product) {
        return product.getCategory() != null
                ? product.getCategory().getName()
                : null;
    }

    default String buildBrandName(Product product) {
        return product.getBrand() != null
                ? product.getBrand().getName()
                : null;
    }


    default List<String> buildImageUrls(Product product) {
        String baseUrl = "https://ecommerce-vinhseo.s3.ap-southeast-2.amazonaws.com/products/";
        return product.getImages().stream()
                .map(image -> baseUrl + product.getId() + "/" + image.getUrl())
                .collect(Collectors.toList());
    }

    @AfterMapping
    default void mapImages(@MappingTarget Product product, ProductRequest request) {
        List<MultipartFile> newImages = request.getNewImages();
        if (newImages != null && !newImages.isEmpty()) {
            List<ProductImage> productImages = newImages.stream().map(image -> {
                ProductImage productImage = new ProductImage();
                String fileName = image.getOriginalFilename();
                productImage.setUrl(fileName);
                productImage.setProduct(product);
                return productImage;
            }).collect(Collectors.toList());
            product.setImages(productImages);
        }
    }
}
