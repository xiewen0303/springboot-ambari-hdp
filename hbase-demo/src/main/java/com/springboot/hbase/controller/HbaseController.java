package com.springboot.hbase.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.springboot.hbase.hbasebean.MobileDataModel;
import com.springboot.hbase.service.HbaseService;
import com.springboot.hbase.service.IHbaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class HbaseController {

    @Resource
    private HbaseService hbaseService;

    @RequestMapping(value = "testHbasePut",produces = {"application/json"})
    public String testHbasePut(){
        JSONObject bean = new JSONObject();
        bean.put("k1","v1");
        bean.put("k2","v2");
        MobileDataModel mobileDataModel = MobileDataModel.builder().phone("1369231").raw(bean).build();
        hbaseService.save(mobileDataModel);

        mobileDataModel = hbaseService.findOneByRowKeyValue(mobileDataModel);
        return JSONObject.toJSONString(mobileDataModel);
    }

    @RequestMapping(value = "testDocker",produces = {"application/json"})
    public String testDocker(){
        JSONObject bean = new JSONObject();
        bean.put("k1","v1");
        bean.put("k2","v2");
        return bean.toJSONString();
    }
}
