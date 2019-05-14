package com.lamdba;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Struct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaLambdaTest {


    public void lambda(){
        Student student = new Student();
        student.setAge(100);
        comTest( build -> {
            System.out.println(build.getAge());
            System.out.println("33333");
        },student);
    }

    public void comTest(ICompare compare,Student student){
        compare.compare(student);
    }

    public static void printStr(String str, Object opb){
        System.out.println(str);
    }

    public static void printStr(String str){
        System.out.println(str);
    }

    public void j8Test(){
        List<String> al = Arrays.asList("a","b","c");
        al.forEach(JavaLambdaTest::printStr);
    }

    public void testMap(){
        HashMap<String,Object> data = new HashMap<>();
        data.forEach(JavaLambdaTest::printStr);
    }

    @Test
    public void testAssertNotNull() throws  Exception{
        String dataStr = null;
        Assert.assertNotNull(dataStr);
        if(dataStr == null){
           throw new Exception("dataStr is null");
        }
        System.out.println("coming ...");
    }
}
