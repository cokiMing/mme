package com.ming.sql;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/5/25.
 */
public interface BaseMapper<T> {

    @Select("${sql}")
    T findOne(@Param("sql") String sql);

    @Select("${sql}")
    List<T> list(@Param("sql") String sql);

    @Update("${sql}")
    int update(@Param("sql") String sql);

    @Insert("${sql}")
    int insert(@Param("sql") String sql);

    @Delete("${sql}")
    int delete(@Param("sql") String sql);

    @Select("${sql}")
    int count(@Param("sql") String sql);

}
