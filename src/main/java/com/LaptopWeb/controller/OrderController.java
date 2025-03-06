package com.LaptopWeb.controller;

import com.LaptopWeb.dto.request.ChangeOrderStatusRequest;
import com.LaptopWeb.dto.request.OrderRequest;
import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.dto.response.OrderDetailResponse;
import com.LaptopWeb.dto.response.OrderResponse;
import com.LaptopWeb.entity.Order;
import com.LaptopWeb.entity.OrderDetail;
import com.LaptopWeb.entity.OrderStatus;
import com.LaptopWeb.mapper.OrderDetailMapper;
import com.LaptopWeb.mapper.OrderMapper;
import com.LaptopWeb.service.OrderService;
import com.LaptopWeb.utils.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final String PAGE_NUMBER = "1";
    private static final String PAGE_SIZE = "10";

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private OrderResponse orderToOrderResponse(Order order) {
        List<OrderDetail> orderDetails = order.getOrderDetails();

        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(orderDetailMapper::toOrderDetailResponse).toList();

        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setOrderDetailResponses(orderDetailResponses);

        return orderResponse;
    }


    @PostMapping /*checked success*/
    public ResponseEntity<?> createOrder(
            @Valid
            @RequestPart("order")OrderRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
//        Map<String, Object> data = (Map<String, Object>) jwt.getClaim("data");
//        Integer userId = (Integer) data.get("id");

        Long userIdLong = (Long) jwt.getClaimAsMap("data").get("id");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;

        Order order = orderService.createOrder(request, userId);

        OrderResponse orderResponse = orderToOrderResponse(order);

        // send message orderResponse after create an order
        simpMessagingTemplate.convertAndSend("/notice/admin", orderResponse);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/userId/{id}")  /*checked success*/
    public ResponseEntity<?> getOrderByUserId(@PathVariable("id") Integer id) {
        List<Order> orders = orderService.getOrderByUserId(id);

        List<OrderResponse> orderResponses = orders.stream().map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/id/{id}")  /*checked success*/
    public ResponseEntity<?> getOrderById(@PathVariable("id") Integer id) {
        Order order = orderService.getOrderById(id);

        OrderResponse orderResponse = orderToOrderResponse(order);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/code/{orderCode}") /*checked success*/
    public ResponseEntity<?> getOrderByOrderCode(@PathVariable("orderCode") String orderCode) {
        Order order = orderService.getOrderByOrderCode(orderCode);

        OrderResponse orderResponse = orderToOrderResponse(order);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        List<Order> orders = orderService.getAll();
        List<OrderResponse> orderResponses = orders.stream().map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/all-page") /*checked success*/
    public ResponseEntity<?> getAllOrders(
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) Integer size,
            @RequestParam(name = "number", defaultValue = PAGE_NUMBER) Integer number,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "desc") String order,
            @RequestParam(name = "keyWord", required = false) String keyWord,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "method", required = false) String method
    ) {
        Page<Order> orderPage = orderService.getAllOrders(number-1, size, sortBy, order, keyWord, status, method);

        List<Order> orders = orderPage.getContent();
        List<OrderResponse> orderResponses = orders.stream().map(orderMapper::toOrderResponse).toList();

        PageInfo pageInfo = PageInfo.builder()
                .page(orderPage.getNumber()+1)
                .size(orderPage.getSize())
                .totalElements(orderPage.getNumberOfElements())
                .totalPages(orderPage.getTotalPages())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }


    @PutMapping("/change-status/{orderCode}") /*checked success*/
    public ResponseEntity<?> updateOrder(
            @PathVariable String orderCode,
            @RequestPart("changeOrderStatus") ChangeOrderStatusRequest request
    ) {
        Order order = orderService.updateStatus(request.getNewOrderStatus(), orderCode);

        OrderResponse orderResponse = orderToOrderResponse(order);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/my-order") /*checked success*/
    public ResponseEntity<?> getMyOrders(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> data = (Map<String, Object>) jwt.getClaim("data");

        String username = (String) data.get("username");

        List<Order> orders = orderService.getMyOrder(username);

        List<OrderResponse> orderResponses = orders.stream().map(this::orderToOrderResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(orderResponses)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{id}") /*checked success*/
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Integer id) {
        orderService.deleteOrder(id);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Delete order successful")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }
}
