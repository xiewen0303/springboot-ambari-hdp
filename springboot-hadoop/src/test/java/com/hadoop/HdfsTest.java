package com.hadoop;

import com.mongo.HdfsService;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class HdfsTest {

    @Test
    public void testHdfs(){

        System.setProperty("HADOOP_USER_NAME", "hdfs");
        Configuration configuration = new Configuration();
        configuration.addResource("cluster/hdfs.xml");
        HdfsService hdfsService = new HdfsService(configuration);

//        boolean flag = hdfsService.checkExists("/data/miaobt/smsbatch/smsbatch");
//        boolean flag =   hdfsService.checkExists("/data/miaobt/smsbatch/smsbatch");
//        boolean flag =  hdfsService.mkdir("mytest0412");
        boolean flag =  hdfsService.mkdir("/data/miaobt/smsbatch/smsbatch");
        System.out.println(flag);

        List<Map<String,Object>> hdfsInfos =  hdfsService.listFiles("/data/miaobt/smsbatch/smsbatch",null);

        System.out.println(hdfsInfos.size());
    }
}
