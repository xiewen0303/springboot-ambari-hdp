package com.hive.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class HiveJdbc {

    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String url = "jdbc:hive2://node2:10000/test_wind";
//private static String url = "jdbc:hive2://node2:10000";
    private static String user = "hive";
    private static String password = "";

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;

    public void init() throws Exception {
        Class.forName(driverName);
        conn = DriverManager.getConnection(url,user,password);
        stmt = conn.createStatement();
    }

    public void showDatabases() throws Exception {
        String sql = "show databases";
        System.out.println("Running: " + sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    public Connection getConn() {
        return conn;
    }

    public void createTable() throws Exception {
        String createTableSQL ="CREATE TABLE IF NOT EXISTS "
                +" student ( id String, name String, "
                +" age int)"
                +" COMMENT 'student details'"
                +" ROW FORMAT DELIMITED"
                +" FIELDS TERMINATED BY '\t'"
                +" LINES TERMINATED BY '\n'"
                +" STORED AS TEXTFILE";

        Connection conn = getConn();
        if(conn == null || conn.isClosed()){
            log.error("");
            return;
        }

       stmt.execute(createTableSQL);
        System.out.println("create table finally !");
    }

    public void closeConn() throws SQLException {
        if(stmt != null){
                stmt.close();
        }
        if(conn != null && !conn.isClosed()){
            conn.close();
        }
    }

}