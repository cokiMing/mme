package com.ming.sql.utils;

import com.ming.sql.annotation.Column;
import com.ming.sql.annotation.TableName;
import com.ming.sql.part.SqlQuery;
import com.ming.sql.part.SqlUpdate;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/28.
 */
public class SqlHelper {

    private static DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成插入语句
     * @param object 插入对象
     * @return
     */
    public static String insert(Object object) {
        TableName tableName = object.getClass().getAnnotation(TableName.class);
        String name = object.getClass().getSimpleName();
        if (tableName != null) {
            name = tableName.value();
        }
        return "INSERT INTO " + name  + transObject2StrForInsert(object);
    }

    /**
     * 批量插入语句
     * @param list 批量插入的数组
     * @return
     */
    public static String batchInsert(List<?> list) {
        if (list == null || list.size() == 0) {
            throw new RuntimeException("list's size must be greater than 0");
        }

        TableName tableName = list.get(0).getClass().getAnnotation(TableName.class);
        String name = list.get(0).getClass().getSimpleName();
        if (tableName != null) {
            name = tableName.value();
        }
        return "INSERT INTO " + name  + transObject2StrForBatchInsert(list);
    }

    /**
     * 更新语句
     * @param sqlQuery   查询条件
     * @param sqlUpdate  更新字段
     * @param clazz      对象类别
     * @return
     */
    public static String update(SqlQuery sqlQuery, SqlUpdate sqlUpdate, Class<?> clazz) {
        return "UPDATE " + getTableName(clazz) + " SET " + sqlUpdate.buildPart() + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 删除语句
     * @param sqlQuery 查询条件
     * @param clazz    对象类别
     * @return
     */
    public static String delete(SqlQuery sqlQuery, Class<?> clazz) {
        return "DELETE FROM " + getTableName(clazz) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 查询单个对象
     * @param sqlQuery 查询条件
     * @param clazz    对象类别
     * @return
     */
    public static String selectOne(SqlQuery sqlQuery, Class<?> clazz) {
        return "SELECT " + resultMap(clazz) + " FROM " + getTableName(clazz) + " WHERE 1 = 1 " + sqlQuery.buildPart() + " LIMIT 0,1";
    }

    /**
     * 查询结果集中的某些字段
     * @param sqlQuery
     * @param tableName
     * @return
     */
    public static String selectField(SqlQuery sqlQuery, String tableName, String field) {
        return "SELECT " + field + " FROM " + tableName + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 查询多个对象
     * @param sqlQuery
     * @param clazz    对象类别
     * @return
     */
    public static String select(SqlQuery sqlQuery, Class<?> clazz) {
        return "SELECT " + resultMap(clazz) + " FROM " + getTableName(clazz) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 查询个数
     * @param sqlQuery 查询条件
     * @param clazz    对象类别
     * @return
     */
    public static String count(SqlQuery sqlQuery, Class<?> clazz) {
        return "SELECT COUNT(*) FROM " + getTableName(clazz) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    private static String resultMap(Class<?> clazz) {
        StringBuilder tempBuilder = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (tempBuilder.length() != 0) {
                tempBuilder.append(",");
            }
            Column column = field.getAnnotation(Column.class);
            String fieldName = field.getName();
            if (column != null) {
                tempBuilder.append(column.value()).append(" AS ").append(fieldName);
            } else {
                tempBuilder.append(fieldName);
            }
        }

        return tempBuilder.toString();
    }

    /**
     * 将对象转换为插入的sql语句片段
     * @param t
     * @param <T>
     * @return
     */
    private static <T> String transObject2StrForInsert(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder fieldBuilder = new StringBuilder("(");
        StringBuilder valueBuilder = new StringBuilder("(");
        for (Field field : fields) {
            field.setAccessible(true);
            fieldBuilder.append(getFieldName(field)).append(",");
            appendFields(field,t,valueBuilder);
        }

        fieldBuilder.deleteCharAt(fieldBuilder.length() - 1);
        valueBuilder.deleteCharAt(valueBuilder.length() - 1);
        fieldBuilder.append(")");
        valueBuilder.append(")");

        return fieldBuilder.append(" VALUES ").append(valueBuilder).toString();
    }

    /**
     * 将对象转换为批量插入的sql语句片段
     * @param list
     * @param <T>
     * @return
     */
    private static <T> String transObject2StrForBatchInsert(List<T> list) {
        if (list == null || list.size() == 0) {
            return "() VALUES ()";
        }
        StringBuilder fieldBuilder = null;
        StringBuilder valueListBuilder = new StringBuilder();
        for (T t : list) {
            Field[] allFields = t.getClass().getDeclaredFields();
            if (fieldBuilder == null) {
                fieldBuilder = new StringBuilder("(");
                for (Field field : allFields) {
                    fieldBuilder.append(getFieldName(field)).append(",");
                }

                fieldBuilder.deleteCharAt(fieldBuilder.length() - 1);
                fieldBuilder.append(")");
            }

            StringBuilder valueBuilder = new StringBuilder("(");
            for (Field field : allFields) {
                field.setAccessible(true);
                appendFields(field,t,valueBuilder);
            }

            valueBuilder.deleteCharAt(valueBuilder.length() - 1);
            valueBuilder.append(")");
            valueListBuilder.append(valueBuilder).append(",");
        }
        valueListBuilder.deleteCharAt(valueListBuilder.length() - 1);

        return fieldBuilder.append(" VALUES ").append(valueListBuilder).toString();
    }

    private static  <T> void appendFields(Field field,T t,StringBuilder stringBuilder) {
        try {
            Object o = field.get(t);
            if (o != null) {
                String value = o.toString();
                if (o instanceof Date) {
                    value = defaultDateFormat.format(o);
                }
                stringBuilder.append("'").append(value).append("'").append(",");
            } else {
                stringBuilder.append("NULL,");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFieldName(Field field) {
        Column column = field.getAnnotation(Column.class);
        String fieldName = field.getName();
        if (column != null) {
            fieldName = column.value();
        }
        return fieldName;
    }

    private static String getTableName(Class<?> clazz) {
        String tableName = clazz.getSimpleName();
        TableName annotation = clazz.getAnnotation(TableName.class);
        if (annotation != null) {
            tableName = annotation.value();
        }

        return tableName;
    }

}
