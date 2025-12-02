package com.granotec.inventory_api.common.exception;

public class BadRequestException extends RuntimeException{

    private final Object data;

    public BadRequestException(String message) {
        super(message);
        this.data = null;
    }

    public BadRequestException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public Object getData(){
        return data;
    }
}

