package com.ming.sql;

import com.alibaba.fastjson.JSONObject;
import com.ming.sql.combination.CombinationPipeline;
import com.ming.sql.part.SqlQuery;
import com.ming.sql.part.SqlUpdate;
import com.ming.sql.utils.SqlHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/6/13.
 */
@Component
public class BaseService<T,M extends BaseMapper<T>> implements ApplicationContextAware {

    protected M mapper;

    private Class<T> clazz;

    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void initContext() {
        if (this.getClass() != BaseService.class) {
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            Type[] types = type.getActualTypeArguments();
            clazz = (Class<T>) types[0];
            mapper = applicationContext.getBean((Class<M>)types[1]);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public T findOne(SqlQuery sqlQuery) {
        return mapper.findOne(SqlHelper.selectOne(sqlQuery, clazz));
    }

    public List<T> find(SqlQuery sqlQuery) {
        return mapper.list(SqlHelper.select(sqlQuery,clazz));
    }

    public List<T> findAll() {
        return mapper.list(SqlHelper.select(SqlQuery.newInstant(),clazz));
    }

    public long count(SqlQuery sqlQuery) {
        return mapper.count(SqlHelper.count(sqlQuery,clazz));
    }

    public long countAll() {
        return mapper.count(SqlHelper.count(SqlQuery.newInstant(),clazz));
    }

    public int insert(T t) {
        return mapper.insert(SqlHelper.insert(t));
    }

    public int updateById(T t,boolean updateNull) {
        return mapper.update(SqlHelper.updateById(t,updateNull));
    }

    public Double sum(SqlQuery sqlQuery,String field) {
        return mapper.sum(SqlHelper.sum(sqlQuery,field,clazz));
    }

    public int batchInsert(List<T> list) {
        return mapper.insert(SqlHelper.batchInsert(list));
    }

    public List<JSONObject> find(CombinationPipeline combinationPipeline) {
        return mapper.listOnJoin(SqlHelper.select(combinationPipeline));
    }

    public int update(SqlQuery sqlQuery, SqlUpdate sqlUpdate) {
        return mapper.update(SqlHelper.update(sqlQuery,sqlUpdate,clazz));
    }

    public int delete(SqlQuery sqlQuery) {
        return mapper.delete(SqlHelper.delete(sqlQuery,clazz));
    }

    public CombinationPipeline createCombination(Class<T> aClass) {
        return new CombinationPipeline<T>(aClass);
    }
}
