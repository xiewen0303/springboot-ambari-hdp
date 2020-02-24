package com.mvc.exception;

//import com.tron.common.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TestException {
    public static String LOCAL_URL = "http://10.0.0.216:8061";

    @Test
    public void testParameterException() throws  Exception {
        Map<String,String> params = new HashMap<>();
//        String resp = HttpClientUtils.doPost(LOCAL_URL+"/test/testParameterException",params);
//        log.info("==================:{}",resp);
    }


    @Test
    public void testBusinessException() throws  Exception {
        Map<String,String> params = new HashMap<>();
//        params.put("target","1");
//        String resp = HttpClientUtils.doPost(LOCAL_URL+"/test/testBusinessException",params);
//        log.info("==================:{}",resp);
    }

}
