package com.LaptopWeb.exception;

public class AppException extends RuntimeException{
    private ErrorApp errorApp;

    public AppException(ErrorApp errorApp) {
        super(errorApp.getMessage());
        this.errorApp = errorApp;
    }

    public ErrorApp getErrorApp() {
        return errorApp;
    }

    public void setErrorApp(ErrorApp errorApp) {
        this.errorApp = errorApp;
    }
}
