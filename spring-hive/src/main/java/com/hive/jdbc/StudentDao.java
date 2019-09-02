package com.hive.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class StudentDao {

    private JdbcTemplate hiveQueueTemplate;

    private HiveJdbc hiveJdbc;

    public List<Student> getStudentAll() {
        List<Student> students = hiveQueueTemplate.queryForList("select * from student",Student.class);
        return students;
    }
}