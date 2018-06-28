package com.ming.sql.part;

import com.ming.sql.exception.EmptyListException;
import com.ming.sql.utils.SqlHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/25.
 */
public class Condition {

    private DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String field;

    private String partStr;

    private Fragment fragment;

    public Condition(String field,Fragment fragment) {
        this.field = field;
        this.fragment = fragment;
    }

    /**
     * 相等
     * @param object
     * @return
     */
    public Fragment equal(Object object) {
        partStr = field + " = " + parseObjectWithQuote(object);
        return returnFragment();
    }

    /**
     * 不相等
     * @param object
     * @return
     */
    public Fragment notEqual(Object object) {
        partStr = field + " != " + parseObjectWithQuote(object);
        return returnFragment();
    }

    /**
     * 为空
     * @return
     */
    public Fragment isNull() {
        partStr = field + " IS NULL ";
        return returnFragment();
    }

    /**
     * 不为空
     * @return
     */
    public Fragment isNotNull() {
        partStr = field + " IS NOT NULL ";
        return returnFragment();
    }

    /**
     * 类似于
     * @param object
     * @return
     */
    public Fragment like(Object object) {
        partStr = field + " LIKE '%" + parseObject(object) + "%'";
        return returnFragment();
    }

    /**
     * 不类似于
     * @param object
     * @return
     */
    public Fragment notLike(Object object) {
        partStr = field + " NOT LIKE '%" + parseObject(object) + "%'";
        return returnFragment();
    }

    /**
     * 大于
     * @param object
     * @return
     */
    public Fragment greaterThan(Object object) {
        partStr = field + " > " + parseObjectWithQuote(object);
        return returnFragment();
    }

    /**
     * 大于等于
     * @param object
     * @return
     */
    public Fragment greaterThanOrEqual(Object object) {
        partStr = field + " >= " + parseObjectWithQuote(object);
        return returnFragment();
    }

    /**
     * 小于
     * @param object
     * @return
     */
    public Fragment lessThan(Object object) {
        partStr = field + " < " + parseObjectWithQuote(object);
        return returnFragment();
    }

    /**
     * 小于等于
     * @param object
     * @return
     */
    public Fragment lessThanOrEqual(Object object) {
        partStr = field + " <= " + parseObjectWithQuote(object);
        return returnFragment();
    }

    /**
     * 以某字符起始
     * @param object
     * @return
     */
    public Fragment startsWith(Object object) {
        partStr = field + " LIKE '" + parseObject(object) + "%'";
        return returnFragment();
    }

    /**
     * 不以某字符起始
     * @param object
     * @return
     */
    public Fragment notStartsWith(Object object) {
        partStr = field + " NOT LIKE '" + parseObject(object) + "%'";
        return returnFragment();
    }

    /**
     * 以某字符结尾
     * @param object
     * @return
     */
    public Fragment endsWith(Object object) {
        partStr = field + " LIKE '%" + parseObject(object) + "'";
        return returnFragment();
    }

    /**
     * 不以某字符结尾
     * @param object
     * @return
     */
    public Fragment notEndsWith(Object object) {
        partStr = field + " NOT LIKE '%" + parseObject(object) + "'";
        return returnFragment();
    }

    /**
     * 属于某个集合
     * @param collection
     * @return
     */
    public Fragment in(Collection<?> collection) {
        if (collection.size() == 0) {
            throw new EmptyListException("collection's size must be greater than 0");
        }

        StringBuilder stringBuilder = new StringBuilder(field);
        stringBuilder.append(" IN (");
        for (Object o : collection) {
            stringBuilder.append(parseObjectWithQuote(o));
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");

        partStr = stringBuilder.toString();
        return returnFragment();
    }

    /**
     * 不属于某个集合
     * @param collection
     * @return
     */
    public Fragment notIn(Collection<?> collection) {
        if (collection.size() == 0) {
            return returnFragment();
        }

        StringBuilder stringBuilder = new StringBuilder(field);
        stringBuilder.append(" NOT IN (");
        for (Object o : collection) {
            stringBuilder.append(parseObjectWithQuote(o));
            stringBuilder.append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");

        partStr = stringBuilder.toString();
        return returnFragment();
    }

    /**
     * 属于某个sql中的集合
     * @param sql
     * @return
     */
    public Fragment in(String sql) {
        StringBuilder stringBuilder = new StringBuilder(field);
        stringBuilder.append(" IN (")
                .append(sql)
                .append(")");

        partStr = stringBuilder.toString();
        return returnFragment();
    }

    /**
     * 不属于某个sql中的集合
     * @param sql
     * @return
     */
    public Fragment notIn(String sql) {
        StringBuilder stringBuilder = new StringBuilder(field);
        stringBuilder.append(" NOT IN (")
                .append(sql)
                .append(")");

        partStr = stringBuilder.toString();
        return returnFragment();
    }

    private String parseObjectWithQuote(Object object) {
        if (object == null) {
            return "NULL";
        }

        String content = object.toString();
        if (object instanceof Date) {
            content = defaultFormat.format(object);
        }
        content = SqlHelper.replaceInvalidChar(content);

        return "'" + content + "'";
    }

    private String parseObject(Object object) {
        if (object == null) {
            return "NULL";
        }

        String content = object.toString();
        if (object instanceof Date) {
            content = defaultFormat.format(object);
        }
        content = SqlHelper.replaceInvalidChar(content);

        return content;
    }

    public Fragment returnFragment() {
        if (partStr != null) {
            if (fragment.fragmentBuilder.length() > 0) {
                fragment.fragmentBuilder.append(" AND ");
            }

            fragment.fragmentBuilder.append(partStr);
        }

        return fragment;
    }
}
