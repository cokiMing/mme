package com.ming.sql.exception;

/**
 * @auto
 * Created by wuyiming on 2018/6/26.
 */
public class IdFieldNotFoundException extends RuntimeException {

    public IdFieldNotFoundException(String message) {
        super(message);
    }

    public IdFieldNotFoundException() {
    }
}
