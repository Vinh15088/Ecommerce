package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.IntrospectTokenRequest;
import com.LaptopWeb.dto.request.LoginRequest;
import com.LaptopWeb.dto.request.LogoutRequest;
import com.LaptopWeb.dto.request.RefreshTokenRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.AuthenticationResponse;
import com.LaptopWeb.service.AuthenticationService;
import com.LaptopWeb.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;


    @PostMapping("/login/local")
    public ApiResponse<?> login(@RequestBody LoginRequest loginRequest) {
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();

        AuthenticationResponse response = authenticationService.authenticationResponse(loginRequest);

        apiResponse.setSuccess(true);
        apiResponse.setContent(response);

        return apiResponse;
    }

    @PostMapping("/outbound/google/authentication")
    public ApiResponse<?> outboundAuthenticationGoogle(@RequestParam("code") String code) {
        var result = authenticationService.outboundAuthenticateGoogle(code);

        return ApiResponse.builder()
                .success(true)
                .content(result)
                .build();
    }

    @PostMapping("/outbound/facebook/authentication")
    public ApiResponse<?> outboundAuthenticationFacebook(@RequestParam("code") String code) {
        var result = authenticationService.outboundAuthenticateFacebook(code);

        return ApiResponse.builder()
                .success(true)
                .content(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<?> introspect(@RequestBody IntrospectTokenRequest request) throws ParseException, JOSEException {
        var response = authenticationService.introspectTokenResponse(request);

        return ApiResponse.builder()
                .success(true)
                .content(response)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);

        return ApiResponse.builder()
                .success(true)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        var response = authenticationService.refreshToken(request);

        return ApiResponse.builder()
                .success(true)
                .content(response)
                .build();
    }

}
