package com.LaptopWeb.exception;

public enum ErrorCode {
    ERROR_LOGIN(1001, "Login error"),
    ERROR_REGISTER(1002, "Register error"),
    ERROR_USER(1003, "User error"),
    ERROR_AUTHENTICATION(1004, "Authentication error"),
    ERROR_BRAND(1005, "Brand error"),
    ERROR_CATEGORY(1006, "Category error"),
    ERROR_PRODUCT(1007, "Product error"),
    ERROR_ORDER(1008, "Order error"),
    ERROR_CART(1009, "Cart error"),
    ERROR_REVIEW(1010, "Review error"),
    ACCESS_DENIED(1011, "Access denied"),


    ;

    private Integer code;
    private String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
