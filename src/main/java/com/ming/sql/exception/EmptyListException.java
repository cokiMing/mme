package com.ming.sql.exception;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/6/26.
 */
public class EmptyListException extends RuntimeException {

    public EmptyListException(String message) {
        super(message);
    }

    public EmptyListException() {
    }
}
