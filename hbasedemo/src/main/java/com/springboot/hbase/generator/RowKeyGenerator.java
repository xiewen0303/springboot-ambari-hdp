package com.springboot.hbase.generator;

public interface RowKeyGenerator<T extends Object> {

    String generateRowKey(T rowKeyParam);

    default T parseRowKey(String rowKey) {
        return null;
    }

}
