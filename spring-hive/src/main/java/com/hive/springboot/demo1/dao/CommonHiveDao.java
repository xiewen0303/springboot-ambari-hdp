package com.hive.springboot.demo1.dao;

import com.hive.springboot.demo1.bean.TemplateStudent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
public class CommonHiveDao {

    @Resource
    private JdbcTemplate hiveJdbcTemplate;

//    public TemplateStudent execute(HiveBean bean){
//        hiveJdbcTemplate.execute(bean.getHql());
//        HiveResponse hiveResponse = HiveResponse.successResult();
//        return hiveResponse;
//    }
//
//    public HiveResponse executeQuery(HiveBean bean){
//        List<TemplateStudent> result = hiveJdbcTemplate.queryForList("",TemplateStudent.class);
//        return HiveResponse.successResult().setListResult(result);
//    }
}
