package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.CreateUserRequest;
import com.LaptopWeb.dto.request.UpdateUserRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.UserResponse;
import com.LaptopWeb.entity.User;
import com.LaptopWeb.mapper.UserMapper;
import com.LaptopWeb.service.UserService;
import com.LaptopWeb.utils.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.LaptopWeb.utils.PageInfo.PAGE_NUMBER;
import static com.LaptopWeb.utils.PageInfo.PAGE_SIZE;

@RestController
@RequestMapping("/users")
public class UserController {



    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody CreateUserRequest request) throws Exception {
        User user = userService.createUser(request);

        UserResponse userResponse = userMapper.toUserResponse(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable("userId") Integer userid) {
        User user = userService.getById(userid);

        UserResponse userResponse = userMapper.toUserResponse(user);

        ApiResponse apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return  ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<User> users = userService.getAll();

        List<UserResponse> listUsers = users.stream().map(userMapper::toUserResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(listUsers)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserPage(
            @RequestParam(name = "pageNumber", defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = PAGE_SIZE ) Integer pageSize,
            @RequestParam(name = "sortField") String sortField,
            @RequestParam(name = "keyWord") String keyWord
    ) {
        Page<User> page = userService.getPageUser(pageNumber-1, pageSize, sortField, keyWord);

        List<User> users = page.getContent();
        List<UserResponse> listUserPage = users.stream().map(userMapper::toUserResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(listUserPage)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser (
            @Valid @RequestBody UpdateUserRequest request,
            @PathVariable("userId") Integer userId
            ) {
        User user = userService.updateUser(userId, request);

        UserResponse userResponse = userMapper.toUserResponse(user);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser (@PathVariable("userId") Integer userId) {
        userService.deleteUser(userId);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Delete User Successfully")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }



}
