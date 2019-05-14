package com.mongo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.net.URI;

public class AppTest  {


    public void testA(){
        System.out.println("=============");
    }


    public void myTest(){
        try {
            FileSystem fileSystem = FileSystem.get(URI.create("hdfs://192.168.0.237:8020/"), new Configuration());
            fileSystem.mkdirs(new Path("hdfs://192.168.0.237:8020/helloworld"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void testCd(){
        long a = 1547606208922l;
        long b = 1547606202872l;

        System.out.println();
        System.out.println(a-b);
    }



}
