package com.LaptopWeb.controller;

import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.VNPayResponse;
import com.LaptopWeb.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/payment/vn-pay")
public class VNPayController {
    private static final Logger log = LoggerFactory.getLogger(VNPayController.class);
    @Autowired
    private VNPayService vnPayService;

    @GetMapping("/payment-order/{orderCode}")
    public ResponseEntity<?> paymentOrder(@PathVariable("orderCode") String orderCode) {
        VNPayResponse vnPayResponse = vnPayService.paymentOrder(orderCode);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(vnPayResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/call-back")
    public ResponseEntity<?> callBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VNPayResponse vnPayResponse = vnPayService.orderReturn(request);

        response.sendRedirect("http://localhost:3000/my-orders/pending");
        return null;
    }

    @GetMapping("/IPN")
    public ResponseEntity<?> IPN(HttpServletRequest request) {
        VNPayResponse vnPayResponse = vnPayService.orderReturn(request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(vnPayResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}