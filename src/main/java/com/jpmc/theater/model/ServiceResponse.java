package com.jpmc.theater.model;

public class ServiceResponse {
    private boolean isSuccess;
    private String message;

    public ServiceResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}
