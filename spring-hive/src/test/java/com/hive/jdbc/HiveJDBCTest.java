package com.hive.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class HiveJDBCTest {
    HiveJdbc hJdbc = new HiveJdbc();

    @Before
    public void testInit(){
        try {
            hJdbc.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConn(){
        try {
            hJdbc.showDatabases();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                hJdbc.closeConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCreateTable(){
        try {
            hJdbc.createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                hJdbc.closeConn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}