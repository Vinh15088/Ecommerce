package com.LaptopWeb.config;

import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ZaloPayConfig {
    public static final String app_id = "2554";
    public static final String key1 = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn";
    public static final String key2 = "trMrHtvjo6myautxDUiAcYsVtaeQ8nhf";
    public static final String endpoint = "https://sb-openapi.zalopay.vn/v2";

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
