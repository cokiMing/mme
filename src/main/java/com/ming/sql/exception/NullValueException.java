package com.ming.sql.exception;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/6/26.
 */
public class NullValueException extends RuntimeException {

    public NullValueException(String message) {
        super(message);
    }

    public NullValueException() {
        super();
    }
}
