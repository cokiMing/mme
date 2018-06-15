package com.ming.sql.part;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/25.
 */
public class SqlQuery extends Fragment{

    private String pageStr;

    private StringBuilder orderBuilder;

    private StringBuilder orBuilder;

    private String built;

    public static SqlQuery newInstant() {
        SqlQuery sqlQuery = new SqlQuery();
        return sqlQuery;
    }

    public static Fragment fragment() {
        return new Fragment();
    }

    private SqlQuery () {

    }

    /**
     * 或条件查询
     * @param fragments 条件
     * @return
     */
    public SqlQuery or(Fragment... fragments) {
        for (Fragment fragment : fragments) {
            if (orBuilder != null) {
                orBuilder.append(" OR ");
            } else {
                orBuilder = new StringBuilder();
            }
            orBuilder.append("(").append(fragment.buildPart()).append(")");
        }
        return this;
    }

    /**
     * 分页
     * @param pageNo    当前页码
     * @param pageSize  每页数量
     * @return
     */
    public SqlQuery page(int pageNo,int pageSize) {
        if (pageNo <= 0 || pageSize <= 0) {
            throw new RuntimeException("pageNo and pageSize must be greater than 0");
        }
        pageStr = " LIMIT " + (pageNo - 1) * pageSize + "," + pageSize;
        return this;
    }

    /**
     * limit 继承自sql的limit指令
     * @param offset 偏移量
     * @param limit  偏移值
     * @return
     */
    public SqlQuery limit(int offset,int limit) {
        if (offset < 0 || limit <= 0) {
            throw new RuntimeException("pageNo and pageSize must be greater than or equal 0");
        }
        pageStr = " LIMIT " + offset + "," + limit;
        return this;
    }

    /**
     * 正序排序
     * @param field 排序字段 字段名称（必须为数据库表中的字段名称）
     * @return
     */
    public SqlQuery orderByAsc(String field) {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder(" ORDER BY ");
        } else {
            orderBuilder.append(",");
        }
        orderBuilder.append(field).append(" ASC");
        return this;
    }

    /**
     * 逆序排序
     * @param field 排序字段 字段名称（必须为数据库表中的字段名称）
     * @return
     */
    public SqlQuery orderByDesc(String field) {
        if (orderBuilder == null) {
            orderBuilder = new StringBuilder(" ORDER BY ");
        } else {
            orderBuilder.append(",");
        }
        orderBuilder.append(field).append(" DESC");
        return this;
    }

    @Override
    public String buildPart() {
        if (built == null) {
            if (fragmentBuilder.length() != 0 && orBuilder != null) {
                fragmentBuilder.append(" OR ");
            }
            if (fragmentBuilder.length() > 0) {
                fragmentBuilder = new StringBuilder(" AND ").append(fragmentBuilder);
            }
            if (orBuilder != null) {
                if (fragmentBuilder.length() == 0) {
                    fragmentBuilder.append(" AND ");
                }
                fragmentBuilder.append(orBuilder);
            }
            if (orderBuilder != null) {
                fragmentBuilder.append(orderBuilder);
            }
            if (pageStr != null) {
                fragmentBuilder.append(pageStr);
            }

            built = fragmentBuilder.toString();
        }
        return built;
    }
}
