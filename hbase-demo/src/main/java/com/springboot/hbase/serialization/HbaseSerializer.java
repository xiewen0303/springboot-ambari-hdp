package com.springboot.hbase.serialization;

public interface HbaseSerializer<T extends Object> {

    byte[] serialize(T t) throws RuntimeException;

    T deserialize(byte[] bytes) throws RuntimeException;
}
