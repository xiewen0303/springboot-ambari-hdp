package com.common.mongo;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class BaseDao {

    @Resource
    private MongoTemplate mongoTemplate;

    public <T> T findById(Object id,Class<T> c){
        return mongoTemplate.findById(id,c);
    }

    public <T> List<T> findAll(Class<T> c){
        return mongoTemplate.findAll(c);
    }

    public <T> T  save(T t){
        return mongoTemplate.save(t);
    }
}
