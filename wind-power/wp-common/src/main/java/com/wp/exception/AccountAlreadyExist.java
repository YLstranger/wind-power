package com.wp.exception;

public class AccountAlreadyExist extends BaseException{
    public AccountAlreadyExist() {
    }

    public AccountAlreadyExist(String msg) {
        super(msg);
    }
}
