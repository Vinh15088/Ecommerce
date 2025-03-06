package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.ZaloPayCallbackRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.ZaloPayResponse;
import com.LaptopWeb.service.ZaloPayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/payment/zalo-pay")
public class ZaloPayController {

    @Autowired
    private ZaloPayService zaloPayService;

    @GetMapping("/pay-order/{orderCode}")
    public ResponseEntity<?> paymentOrderByZaloPay(
            @PathVariable("orderCode") String orderCode,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        String username = (String) jwt.getClaimAsMap("data").get("username");

        ZaloPayResponse payment = zaloPayService.createPaymentByZaloPay(orderCode, username);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(payment)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/call-back")
    public ResponseEntity<?> callBack(@RequestBody ZaloPayCallbackRequest request)
            throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        System.out.println("test callback");

        JSONObject jsonObject = zaloPayService.callBack(request);

        return ResponseEntity.ok(jsonObject.toString());
    }

    @PostMapping("/status-order/{transactionId}")
    public ResponseEntity<?> statusOrder(@PathVariable("transactionId") String transactionId)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, URISyntaxException {

        Map<String, Object> jsonObject = zaloPayService.statusOrderPayment(transactionId);

        return ResponseEntity.ok(jsonObject);
    }

}
