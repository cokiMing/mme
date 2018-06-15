package com.ming.sql.part;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/25.
 */
public class SqlUpdate implements Part {

    private static DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private StringBuilder updateSqlBuilder = new StringBuilder("");

    public static SqlUpdate newInstant() {
        return new SqlUpdate();
    }

    private SqlUpdate() {

    }

    /**
     * 将某字段设置为某个值
     * @param field  字段名称（必须为数据库表中的字段名称）
     * @param value  值
     * @return
     */
    public SqlUpdate set(String field,Object value) {
        String content = value.toString();
        if (value instanceof Date) {
            content = defaultDateFormat.format(value);
        }
        checkBuilder();
        updateSqlBuilder.append(field).append(" = ").append("'").append(content).append("'");
        return this;
    }

    /**
     * 将某字段置空
     * @param field 字段名称（必须为数据库表中的字段名称）
     * @return
     */
    public SqlUpdate unset(String field) {
        checkBuilder();
        updateSqlBuilder.append(field).append(" = ").append("NULL ");
        return this;
    }

    /**
     * 使某字段自增
     * @param field 字段名称（必须为数据库表中的字段名称）
     * @param step  自增幅度
     * @return
     */
    public SqlUpdate inc(String field,double step) {
        checkBuilder();
        updateSqlBuilder.append(field).append(" = ").append(field).append(" + ").append(step);
        return this;
    }

    /**
     * 使某字段递减
     * @param field 字段名称（必须为数据库表中的字段名称）
     * @param step  递减幅度
     * @return
     */
    public SqlUpdate dec(String field,double step) {
        checkBuilder();
        updateSqlBuilder.append(field).append(" = ").append(field).append(" - ").append(step);
        return this;
    }

    @Override
    public String toString() {
        return buildPart();
    }

    private void checkBuilder() {
        if (updateSqlBuilder.length() > 0) {
            updateSqlBuilder.append(",");
        }
    }

    @Override
    public String buildPart() {
        return updateSqlBuilder.toString();
    }

}
