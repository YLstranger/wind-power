package com.wp.exception;

public class RequestLimitException extends BaseException {

    public RequestLimitException() {

    }

    public RequestLimitException(String msg) {
        super(msg);
    }
}
