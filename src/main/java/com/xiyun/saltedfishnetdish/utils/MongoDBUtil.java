package com.xiyun.saltedfishnetdish.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoDBUtil {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入文档
     *
     * @param object 要插入的文档对象
     * @param collectionName 集合名称
     * @return 插入的文档对象
     */
    public <T> T insert(T object, String collectionName) {
        return mongoTemplate.insert(object, collectionName);
    }

    /**
     * 根据ID查询文档
     *
     * @param id 文档ID
     * @param clazz 文档类型
     * @param collectionName 集合名称
     * @return 查询到的文档对象
     */
    public <T> T findById(String id, Class<T> clazz, String collectionName) {
        return mongoTemplate.findById(id, clazz, collectionName);
    }

    /**
     * 查询集合中的所有文档
     *
     * @param clazz 文档类型
     * @param collectionName 集合名称
     * @return 文档列表
     */
    public <T> List<T> findAll(Class<T> clazz, String collectionName) {
        return mongoTemplate.findAll(clazz, collectionName);
    }

    /**
     * 根据条件查询文档
     *
     * @param query 查询条件
     * @param clazz 文档类型
     * @param collectionName 集合名称
     * @return 文档列表
     */
    public <T> List<T> findByQuery(Query query, Class<T> clazz, String collectionName) {
        return mongoTemplate.find(query, clazz, collectionName);
    }

    /**
     * 更新文档
     *
     * @param id 文档ID
     * @param update 更新内容
     * @param clazz 文档类型
     * @param collectionName 集合名称
     * @return 更新后的文档对象
     */
    public <T> T updateById(String id, Update update, Class<T> clazz, String collectionName) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findAndModify(query, update, clazz, collectionName);
    }

    /**
     * 根据ID删除文档
     *
     * @param id 文档ID
     * @param clazz 文档类型
     * @param collectionName 集合名称
     */
    public <T> void deleteById(String id, Class<T> clazz, String collectionName) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, clazz, collectionName);
    }

    /**
     * 根据条件删除文档
     *
     * @param query 查询条件
     * @param clazz 文档类型
     * @param collectionName 集合名称
     */
    public <T> void deleteByQuery(Query query, Class<T> clazz, String collectionName) {
        mongoTemplate.remove(query, clazz, collectionName);
    }
}