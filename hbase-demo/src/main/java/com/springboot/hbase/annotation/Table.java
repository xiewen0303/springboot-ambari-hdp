package com.springboot.hbase.annotation;


import com.springboot.hbase.generator.DefaultRowKeyGenerator;
import com.springboot.hbase.generator.RowKeyGenerator;

import java.lang.annotation.*;

/**
 * @author pc
 *表名注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

	/**
	 * 表名
	 */
	String tableName();
	/**
	 * 列簇名
	 */
	String columnFamilyName();

	/**
	 * rowKey生成器
	 * @return
	 */
	Class<? extends RowKeyGenerator> generator() default DefaultRowKeyGenerator.class;
	
}
