package com.LaptopWeb.service;

import com.LaptopWeb.config.VNPayConfig;
import com.LaptopWeb.dto.response.VNPayResponse;
import com.LaptopWeb.entity.Order;
import com.LaptopWeb.enums.PaymentMethod;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.amazonaws.services.amplify.model.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private static final Logger log = LoggerFactory.getLogger(VNPayService.class);

    private final OrderService orderService;

    public VNPayResponse paymentOrder(String orderCode){

        Order order = orderService.getOrderByOrderCode(orderCode);

        if(order.isPaymentStatus()) throw new AppException(ErrorApp.PAID_ORDER);

        Map vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Locale", VNPayConfig.vnp_Locale);
        vnp_Params.put("vnp_OrderInfo", "Order code: " + order.getOrderCode());
        vnp_Params.put("vnp_OrderType", VNPayConfig.vnp_OrderType);

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        orderService.saveTransactionIdAndPaymentType(orderCode, vnp_TxnRef, PaymentMethod.VNPAY.getName());

        vnp_Params.put("vnp_IpAddr", VNPayConfig.vnp_IpAddr);
        vnp_Params.put("vnp_Amount", String.valueOf(order.getTotalPrice() * 100));
        vnp_Params.put("vnp_CurrCode", VNPayConfig.vnp_CurrCode);

//        String bank_code = request.getBankCode();
//        if (bank_code != null && !bank_code.isEmpty()) {
//            vnp_Params.put("vnp_BankCode", bank_code);
//        }

        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);


        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);


        //Billing
//        vnp_Params.put("vnp_Bill_Mobile", req.getParameter("txt_billing_mobile"));
//        vnp_Params.put("vnp_Bill_Email", req.getParameter("txt_billing_email"));
//        String fullName = (req.getParameter("txt_billing_fullname")).trim();
//        if (fullName != null && !fullName.isEmpty()) {
//            int idx = fullName.indexOf(' ');
//            String firstName = fullName.substring(0, idx);
//            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
//            vnp_Params.put("vnp_Bill_FirstName", firstName);
//            vnp_Params.put("vnp_Bill_LastName", lastName);
//
//        }
//        vnp_Params.put("vnp_Bill_Address", req.getParameter("txt_inv_addr1"));
//        vnp_Params.put("vnp_Bill_City", req.getParameter("txt_bill_city"));
//        vnp_Params.put("vnp_Bill_Country", req.getParameter("txt_bill_country"));
//        if (req.getParameter("txt_bill_state") != null && !req.getParameter("txt_bill_state").isEmpty()) {
//            vnp_Params.put("vnp_Bill_State", req.getParameter("txt_bill_state"));
//        }
//        // Invoice
//        vnp_Params.put("vnp_Inv_Phone", req.getParameter("txt_inv_mobile"));
//        vnp_Params.put("vnp_Inv_Email", req.getParameter("txt_inv_email"));
//        vnp_Params.put("vnp_Inv_Customer", req.getParameter("txt_inv_customer"));
//        vnp_Params.put("vnp_Inv_Address", req.getParameter("txt_inv_addr1"));
//        vnp_Params.put("vnp_Inv_Company", req.getParameter("txt_inv_company"));
//        vnp_Params.put("vnp_Inv_Taxcode", req.getParameter("txt_inv_taxcode"));
//        vnp_Params.put("vnp_Inv_Type", req.getParameter("cbo_inv_type"));


        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException ex) {
                    throw new BadRequestException(ex.getMessage());
                }

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();

        VNPayResponse vnPayResponse = new VNPayResponse();
        vnPayResponse.setPaymentUrl(paymentUrl);
        return vnPayResponse;
    }


    public VNPayResponse orderReturn(HttpServletRequest request) {
        VNPayResponse vnPayResponse = new VNPayResponse();

        try {
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            JSONObject callbackData = new JSONObject(fields);
            log.info("callbackData: {} ", callbackData);

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) fields.remove("vnp_SecureHashType");
            if (fields.containsKey("vnp_SecureHash")) fields.remove("vnp_SecureHash");

            // Check checksum
            String signValue = VNPayConfig.hashAllFields(fields);
            if (signValue.equals(vnp_SecureHash)) {
                String vnp_TxnRef = request.getParameter("vnp_TxnRef");
                String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
                int amount = Integer.parseInt(request.getParameter("vnp_Amount"))/100;

                String orderCode = vnp_OrderInfo.substring(12);

                log.info(orderCode);

                if(orderService.existsOrderByOrderCodeAndTransactionId(orderCode, vnp_TxnRef)) {

                    Order order = orderService.getOrderByOrderCode(orderCode);
                    order = orderService.saveCallbackPayment(order, callbackData.toString());
                    if(amount == order.getTotalPrice()) {

                        boolean paymentStatus = order.isPaymentStatus();
                        if (!paymentStatus) {
                            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                                //Here Code update PaymnentStatus = 1 into your Database
                                orderService.paymentStatus(order, true);
                            } else {
                                // Here Code update PaymnentStatus = 2 into your Database
                                orderService.paymentStatus(order, false);
                            }

                            vnPayResponse.setRspCode("00");
                            vnPayResponse.setMessage("Confirm Success");
                        } else {
                            vnPayResponse.setRspCode("02");
                            vnPayResponse.setMessage("Order already confirmed");
                        }
                    } else {
                        vnPayResponse.setRspCode("04");
                        vnPayResponse.setMessage("Invalid Amount");
                    }
                } else {
                    vnPayResponse.setRspCode("01");
                    vnPayResponse.setMessage("Order not found");
                }
            } else {
                vnPayResponse.setRspCode("97");
                vnPayResponse.setMessage("Invalid Checksum");
            }
        } catch (Exception e) {
            vnPayResponse.setRspCode("99");
            vnPayResponse.setMessage("Unknown error");
        }

        return vnPayResponse;
    }
}
