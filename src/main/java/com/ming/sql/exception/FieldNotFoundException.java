package com.ming.sql.exception;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/7/17.
 */
public class FieldNotFoundException extends RuntimeException {

    public FieldNotFoundException(String message) {
        super(message);
    }

    public FieldNotFoundException() {
    }
}
