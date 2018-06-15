package com.ming.sql.part;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/25.
 */
public class Fragment implements Part {

    protected StringBuilder fragmentBuilder = new StringBuilder();

    /**
     * 选择字段
     * @param field 字段名称（必须为数据库表中的字段名称）
     * @return
     */
    public Condition field(String field) {
        return new Condition(field,this);
    }

    protected Fragment() {

    }

    @Override
    public String buildPart() {
        return fragmentBuilder.toString();
    }
}
