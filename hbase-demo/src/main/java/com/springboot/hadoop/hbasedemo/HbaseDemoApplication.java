package com.springboot.hadoop.hbasedemo;

import com.springboot.hbase.config.HbaseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(value = {"com.springboot.hbase","com.springboot.spring"})
@Import(value = {HbaseConfiguration.class})
public class HbaseDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(HbaseDemoApplication.class, args);
	}
}
