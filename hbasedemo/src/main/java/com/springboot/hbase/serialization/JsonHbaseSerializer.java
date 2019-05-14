package com.springboot.hbase.serialization;

import com.alibaba.fastjson.JSON;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

@Component
public class JsonHbaseSerializer implements HbaseSerializer<Object> {

    @Override
    public byte[] serialize(Object o) throws RuntimeException {
        return Bytes.toBytes(JSON.toJSONString(o));
    }

    @Override
    public Object deserialize(byte[] bytes) throws RuntimeException {
        if(bytes == null || bytes.length == 0) {
            return null;
        }
        return JSON.parseObject(Bytes.toString(bytes));
    }
}
