package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.BrandRequest;
import com.LaptopWeb.dto.response.BrandResponse;
import com.LaptopWeb.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    @Mapping(target = "url_logo", expression = "java(buildLogo(brand))")
    BrandResponse toBrandResponse(Brand brand);

    default String buildLogo(Brand brand) {
        return "https://ecommerce-vinhseo.s3.ap-southeast-2.amazonaws.com/brands/"
                + brand.getId() + "/"
                + brand.getLogo();
    }
}
