package com.springboot.hbase.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Configuration
public class HbaseConfiguration {

    @Resource
    private HbaseProperties hbaseProperties;

    @Bean
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();

        Map<String, String> config = hbaseProperties.getConfig();
        Set<String> keySet = config.keySet();
        for (String key : keySet) {
            configuration.set(key, config.get(key));
        }
        return configuration;
    }

    @Bean
    public HbaseTemplate hbaseTemplate(@Autowired org.apache.hadoop.conf.Configuration configuration){
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setConfiguration(configuration);
        hbaseTemplate.setAutoFlush(true);
        return hbaseTemplate;
    }

}
