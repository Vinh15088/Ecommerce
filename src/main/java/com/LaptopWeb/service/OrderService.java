package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.OrderDetailRequest;
import com.LaptopWeb.dto.request.OrderRequest;
import com.LaptopWeb.entity.*;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.mapper.OrderDetailMapper;
import com.LaptopWeb.mapper.OrderMapper;
import com.LaptopWeb.repository.OrderRepository;
import com.LaptopWeb.repository.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

import java.util.List;
import java.util.TimeZone;

@Service
@EnableMethodSecurity
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderMapper orderMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final EmailService emailService;
    private final CartService cartService;

    private void sendOrderConfirmationEmail(Order order) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(order.getUser().getEmail());
        emailDetails.setSubject("Xác nhận đơn hàng #" + order.getOrderCode());

        // Định dạng ngày từ java.util.Date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String formattedDate = formatter.format(order.getCreatedAt());

        // Tạo nội dung email chi tiết đơn hàng
        StringBuilder productDetails = new StringBuilder();
        for (OrderDetail detail : order.getOrderDetails()) {
            productDetails.append(String.format(
                    "- %s: %d x %d = %d VND\n",
                    detail.getProduct().getName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getTotalPrice()
            ));
        }

        String msgBody = String.format(
                "Xin chào %s, \n\n"
                        + "Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi!\n\n"
                        + "Thông tin đơn hàng của bạn:\n"
                        + "Mã đơn hàng: %s\n"
                        + "Ngày đặt hàng: %s\n"
                        + "Địa chỉ giao hàng: %s\n"
                        + "Số điện thoại liên hệ: %s\n\n"
                        + "Chi tiết sản phẩm:\n%s\n"
                        + "Tổng tiền: %d VND\n"
                        + "Trạng thái thanh toán: %s\n\n"
                        + "Ghi chú: %s\n\n"
                        + "Chúng tôi sẽ liên hệ với bạn sớm để xác nhận đơn hàng.\n\n"
                        + "Trân trọng,\nĐội ngũ hỗ trợ",
                order.getUser().getFullName(),
                order.getOrderCode(),
                formattedDate,
                order.getAddress(),
                order.getPhone(),
                productDetails.toString(),
                order.getTotalPrice(),
                order.isPaymentStatus() ? "Đã thanh toán" : "Chưa thanh toán",
                order.getNote() != null ? order.getNote() : "Không có"
        );

        emailDetails.setMsgBody(msgBody);

        // Gửi email
        emailService.sendSimpleMail(emailDetails);
    }

    @PreAuthorize("hasRole('USER')")
    public Order createOrder(OrderRequest request, Integer userId) {
        OrderStatus orderStatus = orderStatusRepository.findById(1).orElseThrow(); // get status PENDING

        User user = userService.getById(userId);

        List<OrderDetailRequest>  orderDetailRequestList = request.getDetailRequests();

        Order order = orderMapper.toOrder(request);

        List<OrderDetail> orderDetails = orderDetailRequestList.stream().map(
                detailRequest -> {
                    Product product = productService.getProductById(detailRequest.getProductId());

                    OrderDetail orderDetail = orderDetailMapper.toOrderDetail(detailRequest);
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(product);
                    orderDetail.setUnitPrice(Math.toIntExact(product.getPrice()));
                    orderDetail.setTotalPrice((int) (product.getPrice() * detailRequest.getQuantity()));

                    int quantity = -detailRequest.getQuantity();

                    productService.addProductStock(product.getId(), quantity);

                    return orderDetail;
                }
        ).toList();

        order.setOrderStatus(orderStatus);
        order.setUser(user);
        order.setOrderDetails(orderDetails);
        long totalPrice = 0;
        for (OrderDetail orderDetail : orderDetails) {
            totalPrice += orderDetail.getTotalPrice();
        }
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        cartService.deleteAllProductByUsername(user.getUsername());

        sendOrderConfirmationEmail(savedOrder);

        return savedOrder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getOrderByUserId(Integer id) {
        return orderRepository.findByUserId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Order getOrderById(Integer id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.ORDER_NOT_FOUND));
    }

//    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public Order getOrderByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode).orElseThrow(() ->
                new AppException(ErrorApp.ORDER_NOT_FOUND));
    }

    @PreAuthorize("#username == principal.claims['data']['username']")
    public List<Order> getMyOrder(String username) {
        return orderRepository.getAllOrderByUser(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getAllOrders(Integer number, Integer size, String sortBy, String order,
                                    String keyWord, String status, String method) {
        Sort sort = Sort.by(Sort.Direction.valueOf(order.toUpperCase()), sortBy);

        Pageable pageable = PageRequest.of(number, size, sort);

        return orderRepository.findAll(keyWord, status, method, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Order updateStatus(String orderStatus, String orderCode) {
        OrderStatus orderStatus1 = orderStatusRepository.findByName(orderStatus).orElseThrow();

        int update = orderRepository.updateOrderStatus(orderStatus1, orderCode);

        if(update == 1) return getOrderByOrderCode(orderCode);
        else throw new AppException(ErrorApp.UPDATE_ORDER_STATUS_FAIL);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(Integer id) {
        Order order = getOrderById(id);

        orderRepository.deleteById(id);
    }

    public boolean existOrderByOrderCode(String orderCode) {
        return orderRepository.existsByOrderCode(orderCode);
    }

    public boolean existsByTransactionId(String transactionId) {
        return orderRepository.existsByTransactionId(transactionId);
    }

    public Order getOrderByTransactionId(String transactionId) {
        return orderRepository.findByTransactionId(transactionId).orElseThrow(() ->
                new AppException(ErrorApp.ORDER_NOT_FOUND));
    }

    public boolean existsOrderByOrderCodeAndTransactionId(String orderCode, String transactionId) {
        return orderRepository.existsByOrderCodeAndTransactionId(orderCode, transactionId);
    }

    public Order paymentStatus(Order order, boolean paymentStatus) {
        order.setPaymentStatus(paymentStatus);

        return orderRepository.save(order);
    }

    public Order saveTransactionIdAndPaymentType(String orderCode, String transactionId, String paymentType) {
        Order order = getOrderByOrderCode(orderCode);

        order.setTransactionId(transactionId);
        order.setPaymentType(paymentType);

        return orderRepository.save(order);
    }

    public Order saveCallbackPayment(Order order, String callbackData) {
        order.setCallbackPayment(callbackData);

        return orderRepository.save(order);
    }
}
