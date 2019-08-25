package com.springboot.hbase.service;

import java.io.Serializable;
import java.util.List;

/**
 * 操作HBase业务类
 */
public interface IHbaseService {

    <T extends Serializable> void save(final T t);

    <T extends Serializable> void saveBatch(final List<T> list);

    <T extends Serializable> List<T> findAll(final Class<T> clazz);

    <T extends Serializable> T findOneByRowKeyValue(final T condition);

    <T extends Serializable> T findOneByRowKeyValue(final String rowKey, final Class<T> clazz);

    <T extends Serializable> List<T> findByRowKeyValue(final List<String> rowKeyList, final Class<T> clazz);

    <T extends Serializable> List<T> findFromStartToEndRowKey(final Class<T> clazz, String startRowKey, String endRowKey);

    void deleteRow(String tableName, String rowKey, String familyName);

    <T> void deleteColumnFamily(String tableName, String columnName);

    <T> void modifyColumnFamily(String tableName, String columnName);
}
