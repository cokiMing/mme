package com.ming.sql.combination;

import com.ming.sql.exception.FieldNotFoundException;
import com.ming.sql.part.SqlQuery;
import com.ming.sql.utils.SqlHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/7/17.
 */
public class CombinationPipeline<T> {

    private Class<T> clazz;

    private List<String> retainFields = new ArrayList<String>(16);

    private String joinExpression;

    private SqlQuery sqlQuery;

    public CombinationPipeline(Class<T> clazz) {
        this.clazz = clazz;
        addFields(clazz);
    }

    /**
     * 左连接
     * @param aClass   关联的表
     * @param aField  关联字段
     * @return         当前对象
     */
    public CombinationPipeline leftJoin(Class<?> aClass,String onField,String aField) {
        joinExpression = createJoinExpression(aClass,onField,aField," LEFT JOIN ");
        return this;
    }

    /**
     * 右连接
     * @param aClass   关联的表
     * @param onField  关联字段
     * @return         当前对象
     */
    public CombinationPipeline rightJoin(Class<?> aClass,String onField,String aField) {
        joinExpression = createJoinExpression(aClass,onField,aField," RIGHT JOIN ");
        return this;
    }

    /**
     * 内连接
     * @param aClass   关联的表
     * @param onField  关联字段
     * @return         当前对象
     */
    public CombinationPipeline innerJoin(Class<?> aClass,String onField,String aField) {
        joinExpression = createJoinExpression(aClass,onField,aField," INNER JOIN ");
        return this;
    }

    /**
     * 外连接
     * @param aClass   关联的表
     * @param onField  关联字段
     * @return         当前对象
     */
    public CombinationPipeline outerJoin(Class<?> aClass,String onField,String aField) {
        joinExpression = createJoinExpression(aClass,onField,aField," OUTER JOIN ");
        return this;
    }

    /**
     * 查询条件
     * @param sqlQuery 查询对象
     * @return         当前对象
     */
    public CombinationPipeline query(SqlQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
        return this;
    }

    /**
     * 不需要的字段
     * @param aClass 相关的表
     * @param fields 相关的字段
     * @return
     */
    public CombinationPipeline exclude(Class<?> aClass,String... fields) {
        removeFields(aClass,fields);
        return this;
    }

    /**
     * 需要的字段
     * @param aClass 相关的表
     * @param fields 相关的字段
     * @return
     */
    public CombinationPipeline include(Class<?> aClass,String... fields) {
        pickFields(aClass,fields);
        return this;
    }

    private String createJoinExpression(Class<?> aClass,String onField,String aField, String joinOperation) {
        String aClassName = aClass.getSimpleName();
        String aTableName = SqlHelper.getTableName(aClass);
        String className = clazz.getSimpleName();
        String tableName = SqlHelper.getTableName(clazz);
        addFields(aClass);

        StringBuilder stringBuilder;
        if (joinExpression == null) {
            stringBuilder = new StringBuilder(tableName).append(" ").append(className);
        } else {
            stringBuilder = new StringBuilder(joinExpression);
        }

        return stringBuilder.append(joinOperation)
                .append(aTableName)
                .append(" ").append(aClassName)
                .append(" ON ")
                .append(className).append(".").append(onField).append(" = ")
                .append(aClassName).append(".").append(aField).append(" ").toString();
    }

    private void addFields(Class<?> aClass) {
        for (Field field : aClass.getDeclaredFields()) {
            if (SqlHelper.isStatic(field)) {
                continue;
            }
            retainFields.add(aClass.getSimpleName() + "." + field.getName());
        }
    }

    private void removeFields(Class<?> aClass,String... fields) {
        for (String field : fields) {
            String fieldInfo = aClass.getSimpleName() + "." + field;
            boolean remove = retainFields.remove(fieldInfo.intern());
            if (!remove) {
                throw new FieldNotFoundException(fieldInfo);
            }
        }
    }

    private void pickFields(Class<?> aClass,String... fields) {
        List<String> fieldList = Arrays.asList(fields);
        for (Field field : aClass.getDeclaredFields()) {
            if (SqlHelper.isStatic(field)) {
                continue;
            }

            if (fieldList.contains(field.getName())) {
                retainFields.add(aClass.getSimpleName() + "." + field.getName());
            }
        }
    }

    public List<String> getRetainFields() {
        return retainFields;
    }

    public String getJoinExpression() {
        return joinExpression;
    }

    public SqlQuery getSqlQuery() {
        return sqlQuery;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }
}
