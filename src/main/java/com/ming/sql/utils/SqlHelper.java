package com.ming.sql.utils;

import com.ming.sql.annotation.Column;
import com.ming.sql.annotation.Id;
import com.ming.sql.annotation.TableName;
import com.ming.sql.exception.EmptyListException;
import com.ming.sql.exception.IdAnnotationNotFoundException;
import com.ming.sql.part.SqlQuery;
import com.ming.sql.part.SqlUpdate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
     * 更新 (可支持更新空值)
     * @param object
     * @return
     */
    public static String updateById(Object object,boolean updateNull) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        SqlUpdate sqlUpdate = SqlUpdate.newInstant();
        Object idValue = null;
        String idName = null;

        try {
            for (Field field : declaredFields) {
                if (isStatic(field)) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(object);
                if (value != null) {
                    Id idAnnotation = field.getAnnotation(Id.class);
                    if (idAnnotation != null) {
                        idValue = value;
                        idName = field.getName();
                    } else {
                        sqlUpdate.set(field.getName(),value);
                    }
                } else {
                    if (updateNull) {
                        sqlUpdate.unset(field.getName());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (idValue == null) {
            throw new IdAnnotationNotFoundException("Can not find Annotation @Id on any field");
        }

        SqlQuery sqlQuery = SqlQuery.newInstant();
        sqlQuery.field(idName).equal(idValue);
        return update(sqlQuery,sqlUpdate,object.getClass());
    }

    /**
     * 批量插入语句
     * @param list 批量插入的数组
     * @return
     */
    public static String batchInsert(List<?> list) {
        if (list == null || list.size() == 0) {
            throw new EmptyListException("list's size must be greater than 0");
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
        return "UPDATE " + getTableName(clazz,sqlQuery) + " SET " + sqlUpdate.buildPart() + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 删除语句
     * @param sqlQuery 查询条件
     * @param clazz    对象类别
     * @return
     */
    public static String delete(SqlQuery sqlQuery, Class<?> clazz) {
        return "DELETE FROM " + getTableName(clazz,sqlQuery) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 查询单个对象
     * @param sqlQuery 查询条件
     * @param clazz    对象类别
     * @return
     */
    public static String selectOne(SqlQuery sqlQuery, Class<?> clazz) {
        return "SELECT " + resultMap(clazz) + " FROM " + getTableName(clazz,sqlQuery) + " WHERE 1 = 1 " + sqlQuery.buildPart() + " LIMIT 0,1";
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
        return "SELECT " + resultMap(clazz) + " FROM " + getTableName(clazz,sqlQuery) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }


    private static String createResults(List<String> fields) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String field : fields) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(field);
            } else {
                stringBuilder.append(",").append(field);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 查询个数
     * @param sqlQuery 查询条件
     * @param clazz    对象类别
     * @return
     */
    public static String count(SqlQuery sqlQuery, Class<?> clazz) {
        return "SELECT COUNT(*) FROM " + getTableName(clazz,sqlQuery) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    /**
     * 获取总合数
     * @param sqlQuery
     * @param field
     * @param clazz
     * @return
     */
    public static String sum(SqlQuery sqlQuery,String field, Class<?> clazz) {
        return "SELECT SUM(" + field + ") FROM " + getTableName(clazz,sqlQuery) + " WHERE 1 = 1 " + sqlQuery.buildPart();
    }

    private static String resultMap(Class<?> clazz) {
        StringBuilder tempBuilder = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (isStatic(field)) {
                continue;
            }
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
            if (isStatic(field)) {
                continue;
            }

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
                    if (isStatic(field)) {
                        continue;
                    }
                    fieldBuilder.append(getFieldName(field)).append(",");
                }

                fieldBuilder.deleteCharAt(fieldBuilder.length() - 1);
                fieldBuilder.append(")");
            }

            StringBuilder valueBuilder = new StringBuilder("(");
            for (Field field : allFields) {
                if (isStatic(field)) {
                    continue;
                }
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
            field.setAccessible(true);
            Object o = field.get(t);
            if (o != null) {
                String value = o.toString();
                if (o instanceof Date) {
                    value = defaultDateFormat.format(o);
                }
                value = replaceInvalidChar(value);
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

    public static String getTableName(Class<?> clazz,SqlQuery sqlQuery) {
        String tableName = sqlQuery.getTableName();
        if (tableName == null) {
            tableName = clazz.getSimpleName();
            TableName annotation = clazz.getAnnotation(TableName.class);
            if (annotation != null) {
                tableName = annotation.value();
            }
        }

        return tableName;
    }

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static String replaceInvalidChar(String content) {
        content = content.replace("\\","")
                .replace("'","\\'");

        return content;
    }

}
