package com.springboot.hbase.generator;

import com.springboot.hbase.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5RowKeyGenerator implements RowKeyGenerator<String> {

    @Override
    public String generateRowKey(String source) {
        if(StringUtils.isBlank(source)) {
            throw new RuntimeException("Hbase exception: source is blank when generate rowKey.");
        }
        return MD5Util.md5(source).substring(0, 6) + source;
    }

    @Override
    public String parseRowKey(String rowKey) {
        if(StringUtils.isNotBlank(rowKey)) {
            return rowKey.substring(6);
        }
        return null;
    }

}
