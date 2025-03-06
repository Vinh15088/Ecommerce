package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.OrderDetailRequest;
import com.LaptopWeb.dto.response.OrderDetailResponse;
import com.LaptopWeb.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "totalPrice", source = "totalPrice")
    OrderDetail toOrderDetail(OrderDetailRequest request);

    @Mapping(target = "product", source = "product.name")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

}
