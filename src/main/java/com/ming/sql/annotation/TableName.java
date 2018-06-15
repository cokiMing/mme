package com.ming.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/25.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {

    String value();
}
