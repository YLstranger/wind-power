package com.wp.exception;

public class CaptchaTimeoutException extends BaseException {

    public CaptchaTimeoutException() {

    }

    public CaptchaTimeoutException(String msg) {
        super(msg);
    }
}
