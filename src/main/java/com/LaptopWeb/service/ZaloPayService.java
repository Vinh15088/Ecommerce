package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.ZaloPayCallbackRequest;
import com.LaptopWeb.dto.response.ZaloPayResponse;
import com.LaptopWeb.entity.Order;
import com.LaptopWeb.enums.PaymentMethod;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.utils.HMACUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZaloPayService {

    private final OrderService orderService;

    @Value("${zalo-pay.app_id}")
    private String app_id;

    @Value("${zalo-pay.key1}")
    private String key1;

    @Value("${zalo-pay.key2}")
    private String key2;

    @Value("${zalo-pay.endpoint}")
    private String endpoint;

    private String getCurrentTimeString(String format) {

        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public ZaloPayResponse createPaymentByZaloPay(String orderCode, String username) throws IOException {
        Order order = orderService.getOrderByOrderCode(orderCode);

        if(order.isPaymentStatus()) throw new AppException(ErrorApp.PAID_ORDER);

        String app_trans_id = getCurrentTimeString("yyMMdd") + "_" + getRandomNumber(10);

        orderService.saveTransactionIdAndPaymentType(orderCode, app_trans_id, PaymentMethod.ZALOPAY.getName());

        JSONArray item = new JSONArray();
        for(var orderDetail : order.getOrderDetails()) {
            JSONObject orderDetailJson = new JSONObject();

            orderDetailJson.put("itemid", orderDetail.getProduct().getId());
            orderDetailJson.put("itemname", orderDetail.getProduct().getName());
            orderDetailJson.put("itemquantity", orderDetail.getQuantity());
            orderDetailJson.put("itemprice", orderDetail.getTotalPrice());

            item.add(orderDetailJson);
        }

        JSONObject embed_data = new JSONObject();
        embed_data.put("preferred_payment_method", new ArrayList<>());
        embed_data.put("redirecturl", "http://localhost:3000/my-orders/pending"); // url return after payment

        String callbackUrl = "https://9d7a-2405-4803-fe88-65c0-b146-e72f-4d18-761b.ngrok-free.app/api/v1"
                + "/payment/zalo-pay/call-back";


        Map<String, Object> orderNew = new HashMap<>() {{
            put("app_id", app_id);
            put("app_user", username);
            put("app_trans_id", app_trans_id);
            put("app_time", System.currentTimeMillis());
            put("expire_duration_seconds", 900);
            put("amount", order.getTotalPrice());
            put("description", "Thanh toan don hang #" + orderCode);
            put("embed_data", embed_data.toString());
            put("bank_code", "");
            put("item", item);
            put("phone", order.getPhone());
            put("address", order.getAddress());
            put("callback_url", callbackUrl);
        }};


        String data = orderNew.get("app_id") +"|"+ orderNew.get("app_trans_id") +"|"+
                orderNew.get("app_user") +"|"+ orderNew.get("amount") +"|"+
                orderNew.get("app_time") +"|"+ orderNew.get("embed_data") +"|"+ orderNew.get("item");
        orderNew.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(endpoint + "/create");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for(Map.Entry<String, Object> entry : orderNew.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));

            System.out.println(entry.getKey() + " " + entry.getValue().toString());
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();

        String line;
        while ((line = rd.readLine()) != null) resultJsonStr.append(line);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(resultJsonStr.toString(), ZaloPayResponse.class);
    }

    public JSONObject callBack(ZaloPayCallbackRequest request)
            throws NoSuchAlgorithmException, InvalidKeyException {
        System.out.println(request.toString());

        JSONObject jsonObject = new JSONObject();

        Mac HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(key2.getBytes(), "HmacSHA256"));

        System.out.println("0");
        try {
            ObjectMapper mapper = new ObjectMapper();

            System.out.println("1");
            ZaloPayCallbackRequest.ZaloPayCallbackData data =
                    mapper.readValue(request.getData(), ZaloPayCallbackRequest.ZaloPayCallbackData.class);

            System.out.println(data);

            String reqmac = request.getMac();
            String dataStr = request.getData();

            System.out.println(reqmac);

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            System.out.println(mac);

            String transactionId = data.getAppTransactionId();

            // check if the callback is valid (from ZaloPay server)
            if (!reqmac.equals(mac)) {
                // callback is invalid
                jsonObject.put("returncode", -1);
                jsonObject.put("returnmessage", "mac not equal");
            } else {
                // payment success
                // merchant update status for order's status
                Order order = orderService.getOrderByTransactionId(transactionId);
                String callbackData = mapper.writeValueAsString(data);

                orderService.paymentStatus(order, true);
                orderService.saveCallbackPayment(order, callbackData);

                log.info("update order's status = success where app_trans_id = " + data.getAppTransactionId());

                jsonObject.put("return_code", 1);
                jsonObject.put("return_message", "success");
            }
        } catch (Exception ex) {
            jsonObject.put("return_code", 0); // callback again (up to 3 times)
            jsonObject.put("return_message", ex.getMessage());
        }

        return jsonObject;
    }

    public Map<String, Object> statusOrderPayment(String app_trans_id) throws URISyntaxException, IOException {
//        String app_trans_id = "210608_2553_1623145380738";  // Input your app_trans_id
        String data = app_id +"|"+ app_trans_id  +"|"+ key1; // appid|app_trans_id|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", app_id));
        params.add(new BasicNameValuePair("app_trans_id", app_trans_id));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(endpoint + "/query");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(resultJsonStr.toString(), JSONObject.class);
    }
}


