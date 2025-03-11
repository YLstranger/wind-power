package com.wp.exception;

public class CaptchaErrorException extends BaseException{

    public CaptchaErrorException(){

    }

    public CaptchaErrorException(String message) {
        super(message);
    }

}
