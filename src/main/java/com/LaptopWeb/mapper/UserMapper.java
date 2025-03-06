package com.LaptopWeb.mapper;

import com.LaptopWeb.dto.request.CreateUserRequest;
import com.LaptopWeb.dto.request.UpdateUserRequest;
import com.LaptopWeb.dto.response.UserResponse;
import com.LaptopWeb.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequest request);

    UserResponse toUserResponse(User user);

    User toUserUpdate(UpdateUserRequest request);


}
