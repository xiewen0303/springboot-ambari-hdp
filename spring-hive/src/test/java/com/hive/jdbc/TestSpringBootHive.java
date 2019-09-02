package com.hive.jdbc;

import com.hive.springboot.HiveApplication;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HiveApplication.class)
public class TestSpringBootHive {

    @Resource
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testConn(){
        try {
            List<Student> studentList = jdbcTemplate.query("select * from student",new BeanPropertyRowMapper<>(Student.class));
            log.info("studentList={}", JSONArray.toJSONString(studentList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
