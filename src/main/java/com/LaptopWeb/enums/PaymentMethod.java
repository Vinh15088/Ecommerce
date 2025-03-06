package com.LaptopWeb.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    VNPAY("VNPAY"),
    ZALOPAY("ZALOPAY");

    private String name;

    PaymentMethod(String name) {
        this.name = name;
    }
}
