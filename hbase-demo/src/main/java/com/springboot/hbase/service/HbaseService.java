package com.springboot.hbase.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.springboot.hbase.annotation.Column;
import com.springboot.hbase.annotation.RowKey;
import com.springboot.hbase.generator.RowKeyGenerator;
import com.springboot.hbase.serialization.HbaseSerializer;
import com.springboot.hbase.serialization.StringHbaseSerializer;
import com.springboot.hbase.util.ReflectionUtils;
import com.springboot.spring.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.ResultsExtractor;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.Transient;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.*;

@Service
@Slf4j
public class HbaseService implements IHbaseService {

    @Resource
    private HbaseTemplate hbaseTemplate;

    /**
     * 检查并设置值。
     *
     * @param tableName  表名
     * @param rowName    行键
     * @param familyName 列族
     * @param qualifier  列
     * @param expect     期望值，用于比较
     * @param value      设置值，只要在该列的值和期望值一致时才会设置
     * @return true: 期望值和列值一致，设置value成功。false:表示设置失败
     */
    private boolean checkAndPut(String tableName, final String rowName, final String familyName, final String qualifier, final String expect, final String value) {
        return hbaseTemplate.execute(tableName, htable -> {
            Put put = new Put(rowName.getBytes(getCharset())).add(familyName.getBytes(getCharset()), qualifier.getBytes(getCharset()), value.getBytes(getCharset()));
            return htable.checkAndPut(rowName.getBytes(getCharset()), familyName.getBytes(getCharset()), qualifier.getBytes(getCharset()), expect.getBytes(getCharset()), put);
        });
    }

    /**
     * 检查并删除值
     *
     * @param tableName  表名
     * @param rowName    行键
     * @param familyName 列族
     * @param qualifier  列
     * @param expect     期望值，用于比较
     * @return true: 期望值和列值一致，删除成功。false:表示删除失败
     */
    private boolean checkAndDelete(String tableName, final String rowName, final String familyName, final String qualifier, final String expect) {
        return hbaseTemplate.execute(tableName, htable -> {
            Delete delete = new Delete(rowName.getBytes(getCharset())).addColumns(familyName.getBytes(getCharset()), qualifier.getBytes(getCharset()));
            return htable.checkAndDelete(rowName.getBytes(getCharset()), familyName.getBytes(getCharset()), qualifier.getBytes(getCharset()), expect.getBytes(getCharset()), delete);
        });
    }

    /**
     * 以原子方式设置为给定值，并返回以前的值。
     *
     * @param tableName  表名
     * @param rowName    行键
     * @param columnFamilyName 列族
     * @param qualifier  列
     * @param value      设置值，只要在该列的值和期望值一致时才会设置
     * @return
     */
    public String getAndPut(String tableName, final String rowName, final String columnFamilyName, final String qualifier, final String value) {
        String oldValue = null;
        do {
            oldValue = hbaseTemplate.get(tableName, rowName, columnFamilyName, qualifier, (result, rowNum) ->
                    new String(result.getValue(columnFamilyName.getBytes(getCharset()), qualifier.getBytes(getCharset())), getCharset())
            );
        } while (checkAndPut(tableName, rowName, columnFamilyName, qualifier, oldValue, value));
        return oldValue;
    }

    /**
     * 以原子方式删除值，并返回以前的值。
     *
     * @param tableName  表名
     * @param rowName    行键
     * @param familyName 列族
     * @param qualifier  列
     * @return
     */
    public String getAndDelete(String tableName, final String rowName, final String familyName, final String qualifier) {
        String oldValue = null;
        do {
            oldValue = hbaseTemplate.get(tableName, rowName, familyName, qualifier, (RowMapper<String>) (result, rowNum) ->
                    new String(result.getValue(familyName.getBytes(getCharset()), qualifier.getBytes(getCharset())), getCharset())
            );
        } while (checkAndDelete(tableName, rowName, familyName, qualifier, oldValue));
        return oldValue;
    }

    private Charset getCharset(){
        return hbaseTemplate.getCharset();
    }

    private String generateRowKey(Class<? extends RowKeyGenerator> generatorClass, Map<String, Object> rowKeyParamMap) {
        RowKeyGenerator rowKeyGenerator = (RowKeyGenerator) SpringBeanUtil.getBean(generatorClass);
        Class<?> parameterType = null;
        Method[] methods = generatorClass.getDeclaredMethods();
        for(Method method: methods) {
            if("generateRowKey".equals(method.getName()) && !method.isBridge()) {
                parameterType = method.getParameterTypes()[0];
                break;
            }
        }
        if(parameterType == null) {
            throw new NullPointerException("RowKeyGenerator.generateRowKey method parameter type is null");
        }
        if(parameterType.isAssignableFrom(String.class) || parameterType.isAssignableFrom(Number.class)) {
            Object rowKeyParam = null;
            Set<String> keys = rowKeyParamMap.keySet();
            for(String key: keys) {
                rowKeyParam = rowKeyParamMap.get(key);
            }
            return rowKeyGenerator.generateRowKey(rowKeyParam);
        } else if(parameterType.isAssignableFrom(JSON.class)) {
            return rowKeyGenerator.generateRowKey(JSONObject.parseObject(JSON.toJSONString(rowKeyParamMap)));
        } else if(parameterType.isAssignableFrom(Map.class)) {
            return rowKeyGenerator.generateRowKey(rowKeyParamMap);
        } else {
            JSONObject json = JSONObject.parseObject(JSON.toJSONString(rowKeyParamMap));
            return rowKeyGenerator.generateRowKey(JSONObject.toJavaObject(json, parameterType));
        }
    }

    private <T extends Serializable> void parseRowKey(Class<? extends RowKeyGenerator> generatorClass, String rowKey, T t) {
        RowKeyGenerator rowKeyGenerator = (RowKeyGenerator) SpringBeanUtil.getBean(generatorClass);
        Object result = rowKeyGenerator.parseRowKey(rowKey);
        if(result != null) {
            final JSONObject json;
            final boolean isBasicType = result instanceof String || result instanceof Number;
            if(isBasicType) {
                json = new JSONObject();
                json.put("rowKey", result);
            } else {
                json = JSONObject.parseObject(JSON.toJSONString(result));
            }
            ReflectionUtils.doWithLocalFields(t.getClass(), field -> {
                RowKey rowKeyAnnotation = field.getAnnotation(RowKey.class);
                if (rowKeyAnnotation != null) {
                    fieldPreHandle(field);
                    if(isBasicType) {
                        field.set(t, json.get("rowKey"));
                    } else {
                        String rowKeyName = StringUtils.isBlank(rowKeyAnnotation.name()) ? field.getName() : rowKeyAnnotation.name();
                        field.set(t, json.get(rowKeyName));
                    }
                }
            });
        }
    }

    public <T extends Serializable> void save(final T t) {
        if (t == null) {
            throw new IllegalArgumentException("实体对象t不能为空");
        }
        final com.springboot.hbase.annotation.Table table = t.getClass().getAnnotation(com.springboot.hbase.annotation.Table.class);

        checkTableAnnotation(t, table);

        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();

        createTableIfNotExist(columnFamilyName, tableName);

        final Map<String/* columnName */, byte[]/* field值 */> map = new HashMap<>();
        final Map<String, Object> rowKeyParamMap = new HashMap<>();

        // 遍历field
        doWithFields(t, map, rowKeyParamMap);

        byte[] rowKey = Bytes.toBytes(generateRowKey(table.generator(), rowKeyParamMap));
        hbaseTemplate.execute(tableName, hTable -> {
            Put p = new Put(rowKey);
            Set<Map.Entry<String, byte[]>> entrySet = map.entrySet();
            Iterator<Map.Entry<String, byte[]>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, byte[]> entry = iterator.next();
                p.addColumn(Bytes.toBytes(columnFamilyName), Bytes.toBytes(entry.getKey()), entry.getValue());
            }
            hTable.put(p);
            return t;
        });
    }

    private <T extends Serializable> void doWithFields(T t, Map<String, byte[]> map, Map<String, Object> rowKeyParamMap) {
        ReflectionUtils.doWithLocalFields(t.getClass(), field -> {
            RowKey rowKey = field.getAnnotation(RowKey.class);
            if(rowKey != null) {
                fieldPreHandle(field);
                Object fieldValue = ReflectionUtils.getField(field, t);
                if (fieldValue != null) {
                    String rowKeyName = StringUtils.isBlank(rowKey.name()) ? field.getName() : rowKey.name();
                    rowKeyParamMap.put(rowKeyName, fieldValue);
                }
            } else {
                Transient skipAnno = field.getAnnotation(Transient.class);
                if(skipAnno != null) return;

                fieldPreHandle(field);
                Object fieldValue = ReflectionUtils.getField(field, t);
                if (fieldValue != null) {
                    Column column = field.getAnnotation(Column.class);
                    HbaseSerializer<Object> serializer;
                    String columnName;
                    if(column != null) {
                        serializer =   SpringBeanUtil.getBean(column.serializer());
                        columnName = column.columnName();
                    } else {
                        serializer =  SpringBeanUtil.getBean(StringHbaseSerializer.class);
                        columnName = field.getName();
                    }
                    map.put(columnName, serializer.serialize(fieldValue));
                }
            }
        });
    }

    private <T extends Serializable> void checkTableAnnotation(final T t, final com.springboot.hbase.annotation.Table table) {
        if (table == null) {
            throw new IllegalArgumentException("请检查" + t.getClass().getName() + "注解@Table是否添加");
        }
    }

    public <T extends Serializable> void saveBatch(final List<T> list) {
        if (list == null || list.size() == 0) {
            throw new IllegalArgumentException("list不能为空");
        }
        int size = list.size();
        // 取第一个，获取表上的注解
        T first = list.get(0);
        final com.springboot.hbase.annotation.Table table = first.getClass().getAnnotation(com.springboot.hbase.annotation.Table.class);
        if (table == null) {
            throw new IllegalArgumentException("请检查" + first.getClass().getName() + "注解@Table是否添加");
        }

        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();

        createTableIfNotExist(columnFamilyName, tableName);

        List<Put> putList = new ArrayList<Put>();
        for (int i = 0; i < size; i++) {
            T t = list.get(i);

            final Map<String/* columnName */, byte[]/* field值 */> map = new HashMap<>();
            final Map<String, Object> rowKeyParamMap = new HashMap<>();

            // 遍历field
            doWithFields((T) t, map, rowKeyParamMap);

            byte[] rowKey = Bytes.toBytes(generateRowKey(table.generator(), rowKeyParamMap));
            Put p = new Put(rowKey);
            Set<Map.Entry<String, byte[]>> entrySet = map.entrySet();
            Iterator<Map.Entry<String, byte[]>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, byte[]> entry = iterator.next();
                p.addColumn(Bytes.toBytes(columnFamilyName), Bytes.toBytes(entry.getKey()), entry.getValue());
            }
            putList.add(p);
        }

        // 批量插入
        hbaseTemplate.execute(tableName, (TableCallback<T>) hTable -> {
            hTable.put(putList);
            return null;
        });
    }

    private void createTableIfNotExist(final String columnFamilyName, final String tableName) {
        // 判断表是否存在
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            HColumnDescriptor columnDescriptor = null;
            if (!admin.tableExists(tableName)) {
                HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
                columnDescriptor = new HColumnDescriptor(columnFamilyName);
                tableDescriptor.addFamily(columnDescriptor);
                admin.createTable(tableDescriptor);
                checkTableAndEnable(tableName, admin);

            } else {
                HTableDescriptor tableDescriptor = admin.getTableDescriptor(TableName.valueOf(tableName));
                HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
                if (columnFamilies == null || columnFamilies.length == 0) {
                    columnDescriptor = new HColumnDescriptor(columnFamilyName);
                } else {
                    String[] columnFamiliesNames = new String[columnFamilies.length];
                    for (int i = 0; i < columnFamilies.length; i++) {
                        columnFamiliesNames[i] = columnFamilies[i].getNameAsString();
                    }
                    if (Arrays.asList(columnFamiliesNames).contains(columnFamilyName)) {
                        // 不再添加已经存在的列
                        log.info("{}增加新的ColumnFamily:[{}]已经存在，所以不再添加新的columFamily", tableName, columnFamilyName);
                        return;
                    } else {
                        // 修改表结构
                        columnDescriptor = new HColumnDescriptor(columnFamilyName);
                        tableDescriptor.addFamily(columnDescriptor);
                        admin.disableTable(tableName);
                        /**
                         * modifyTable只提供了异步的操作模式，如果需要确认修改是否已成功
                         * 需要在客户端代码中显示循环调用getTableDescriptor()获取元数据 知道结果与本地实例匹配
                         */
                        admin.modifyTable(tableName, tableDescriptor);
                        admin.enableTable(tableName);
                        // 获取远程元数据的HTableDescriptor对象
                        HTableDescriptor tableDescriptorFromMetaData = admin.getTableDescriptor(TableName.valueOf(tableName));
                        int count = 0;
                        while (true) {
                            /**
                             * 比较客户端本地的实例与从元数据获取的实例是否一致(包括所有列簇以及与他们相关的设置)
                             */
                            if (tableDescriptor.equals(tableDescriptorFromMetaData)) {
                                log.info("{}增加新的ColumnFamily:[{}]修改成功", tableName, columnFamilyName);
                                break;
                            } else {
                                log.warn("{}增加新的ColumnFamily:[{}]没有增加成功,继续循环等待异步返回", tableName, columnFamilyName);
                                try {
                                    count++;
                                    if (count == 10) {// 等待10秒如果还没有创建成功，就退出
                                        throw new RuntimeException(tableName + "{}增加新的ColumnFamily:[" + columnFamilyName + "]没有增加成功");
                                    }
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    private void checkTableAndEnable(final String tableName, HBaseAdmin admin) throws IOException {
        boolean tableAvailable = admin.isTableAvailable(tableName);
        boolean tableEnabled = admin.isTableEnabled(tableName);
        if (tableAvailable && tableEnabled) {
            // 如果表不可用，先置为可用
            admin.enableTable(tableName);
        }
    }

    /**
     * 查找最近一个版本的一条数据 通过rowName的值查找 rowkey必须是字符串，并且是字符串序列化方式才可以使用此方法获取
     */
    public <T extends Serializable> T findOneByRowKeyValue(final T condition) {
        final Class<T> clazz = (Class<T>) condition.getClass();
        final com.springboot.hbase.annotation.Table table = clazz.getAnnotation(com.springboot.hbase.annotation.Table.class);
        if (table != null) {
            try {
                final String columnFamilyName = table.columnFamilyName();
                final String tableName = table.tableName();
                final T newInstance = clazz.newInstance();
                final Map<Field/* field */, byte[]/* columnName值 */> map = new HashMap<>();
                final Map<Field/* field */, HbaseSerializer<?>/* 序列化方案 */> serializerMap = new HashMap<>();
                final Map<String, Object> rowKeyParamMap = new HashMap<>();
                // 遍历field
                ReflectionUtils.doWithLocalFields(clazz, field -> {
                    RowKey rowKey = field.getAnnotation(RowKey.class);
                    fieldPreHandle(field);
                    if(rowKey != null) {
                        Object fieldValue = ReflectionUtils.getField(field, condition);
                        if (fieldValue != null) {
                            String rowKeyName = StringUtils.isBlank(rowKey.name()) ? field.getName() : rowKey.name();
                            rowKeyParamMap.put(rowKeyName, fieldValue);
                            field.set(newInstance, fieldValue);
                        }
                    } else {
                        Column column = field.getAnnotation(Column.class);
                        if(column != null) {
                            map.put(field, Bytes.toBytes(column.columnName()));
                            serializerMap.put(field,  SpringBeanUtil.getBean(column.serializer()));
                        } else {
                            map.put(field, Bytes.toBytes(field.getName()));
                            serializerMap.put(field,  SpringBeanUtil.getBean(StringHbaseSerializer.class));
                        }
                    }
                });

                byte[] rowKey = Bytes.toBytes(generateRowKey(table.generator(), rowKeyParamMap));
                T t = hbaseTemplate.get(tableName, Bytes.toString(rowKey), columnFamilyName, (result, rowNum) -> {
                    byte[] row = result.getRow();
                    //防止当T中的属性有初始化值时，是可以获取到的对象数据的，但是在hbase中是没有数据的
                    if(row==null&&result.isEmpty()){
                        return null;
                    }
                    if(row != null) {
                        parseRowKey(table.generator(), Bytes.toString(row), newInstance);
                    }
                    Set<Map.Entry<Field, byte[]>> entrySet = map.entrySet();
                    Iterator<Map.Entry<Field, byte[]>> iterator = entrySet.iterator();
                    setFieldVal(serializerMap, columnFamilyName, result, (T) newInstance, iterator);
                    return newInstance;
                });
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        } else {
            log.warn("{}没有指定@Table注解", clazz.getName());
        }
        return null;
    }

    public <T extends Serializable> T findOneByRowKeyValue(String rowKey, final Class<T> clazz) {
        final com.springboot.hbase.annotation.Table table = clazz.getAnnotation(com.springboot.hbase.annotation.Table.class);
        if (table != null) {
            try {
                final String columnFamilyName = table.columnFamilyName();
                final String tableName = table.tableName();
                final T newInstance = clazz.newInstance();
                final Map<Field/* field */, byte[]/* columnName值 */> map = new HashMap<>();
                final Map<Field/* field */, HbaseSerializer<?>/* 序列化方案 */> serializerMap = new HashMap<>();
                // 遍历field
                doFields(clazz, map, serializerMap);
//                ReflectionUtils.doWithLocalFields(clazz, field -> {
//                    fieldPreHandle(field);
//                    Column column = field.getAnnotation(Column.class);
//                    if(column != null) {
//                        map.put(field, Bytes.toBytes(column.columnName()));
//                        serializerMap.put(field, (HbaseSerializer<?>) SpringBeanUtil.getBean(column.serializer()));
//                    } else {
//                        map.put(field, Bytes.toBytes(field.getName()));
//                        serializerMap.put(field, (HbaseSerializer<?>) SpringBeanUtil.getBean(StringHbaseSerializer.class));
//                    }
//                });

                if (rowKey == null || "".equals(rowKey.trim())) {
                    throw new IllegalArgumentException("rowKey为空");
                }
                T t = hbaseTemplate.get(tableName, rowKey, columnFamilyName, (result, rowNum) -> {
                    byte[] row = result.getRow();
                    //防止当T中的属性有初始化值时，是可以获取到的对象数据的，但是在hbase中是没有数据的
                    if(row==null&&result.isEmpty()){
                        return null;
                    }
                    parseRowKey(table.generator(), rowKey, newInstance);
                    Set<Map.Entry<Field, byte[]>> entrySet = map.entrySet();
                    Iterator<Map.Entry<Field, byte[]>> iterator = entrySet.iterator();
                    setFieldVal(serializerMap, columnFamilyName, result, (T) newInstance, iterator);
                    return newInstance;
                });
                return t;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.warn("{}没有指定@Table注解", clazz.getName());
        }
        return null;
    }

    @Override
    public <T extends Serializable> List<T> findByRowKeyValue(List<String> rowKeyList, Class<T> clazz) {
        if (rowKeyList == null || rowKeyList.isEmpty()) {
            throw new IllegalArgumentException("rowKey为空");
        }
        if(clazz == null) {
            throw new IllegalArgumentException("clazz为空");
        }
        final com.springboot.hbase.annotation.Table table = clazz.getAnnotation(com.springboot.hbase.annotation.Table.class);
        if (table == null) {
            log.error("{}没有指定@Table注解", clazz.getName());
            return null;
        }
        final Map<Field, byte[]> map = new HashMap<>();
        final Map<Field, HbaseSerializer<?>> serializerMap = new HashMap<>();
        // 遍历field
        doFields(clazz, map, serializerMap);

        // 构建GET对象
        List<Get> getList = new ArrayList<>();
        for(String rowKey: rowKeyList) {
            getList.add(new Get(Bytes.toBytes(rowKey)));
        }
        // 执行查询
        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();
        return hbaseTemplate.execute(tableName, hTable -> {
            Result[] results = hTable.get(getList);
            if(results == null || results.length == 0) {
                return null;
            }
            List<T> tList = new ArrayList<>();
            for(Result result: results) {
                byte[] row = result.getRow();
                // 防止当T中的属性有初始化值时，是可以获取到的对象数据的，但是在hbase中是没有数据的
                if(row==null&&result.isEmpty()){
                    continue;
                }
                final T newInstance = clazz.newInstance();
                if(row != null) {
                    parseRowKey(table.generator(), Bytes.toString(row), newInstance);
                }
                Set<Map.Entry<Field, byte[]>> entrySet = map.entrySet();
                Iterator<Map.Entry<Field, byte[]>> iterator = entrySet.iterator();
                setFieldVal(serializerMap, columnFamilyName, result, newInstance, iterator);
                tList.add(newInstance);
            }
            return tList;
        });
    }

    private <T extends Serializable> void doFields(Class<T> clazz, Map<Field, byte[]> map, Map<Field, HbaseSerializer<?>> serializerMap) {
        ReflectionUtils.doWithLocalFields(clazz, field -> {
            fieldPreHandle(field);
            Column column = field.getAnnotation(Column.class);
            if(column != null) {
                map.put(field, Bytes.toBytes(column.columnName()));
                serializerMap.put(field, (HbaseSerializer<?>) SpringBeanUtil.getBean(column.serializer()));
            } else {
                map.put(field, Bytes.toBytes(field.getName()));
                serializerMap.put(field, (HbaseSerializer<?>) SpringBeanUtil.getBean(StringHbaseSerializer.class));
            }
        });
    }

    private <T extends Serializable> void setFieldVal(Map<Field, HbaseSerializer<?>> serializerMap, String columnFamilyName, Result result, T newInstance, Iterator<Map.Entry<Field, byte[]>> iterator) throws IllegalAccessException {
        while (iterator.hasNext()) {
            Map.Entry<Field, byte[]> entry = iterator.next();
            Field field = entry.getKey();
            byte[] columnName = entry.getValue();
            byte[] fieldValue = result.getValue(Bytes.toBytes(columnFamilyName), columnName);
            if (fieldValue != null) {
                field.set(newInstance, serializerMap.get(field).deserialize(fieldValue));
            }
        }
    }

    /**
     * 目前不支持静态属性
     */
    public <T extends Serializable> List<T> findAll(final Class<T> clazz) {
        final com.springboot.hbase.annotation.Table table = clazz.getAnnotation(com.springboot.hbase.annotation.Table.class);
        if (table != null) {
            final String columnFamilyName = table.columnFamilyName();
            final String tableName = table.tableName();
            List<T> list = hbaseTemplate.find(tableName, columnFamilyName, (result, rowNum) -> {
                final T newInstance = clazz.newInstance();
                byte[] row = result.getRow();
                if(row != null) {
                    parseRowKey(table.generator(), Bytes.toString(row), newInstance);
                }
                findExt(clazz, columnFamilyName, result, newInstance);
                return newInstance;
            });

            return list;
        } else {
            log.warn("{}没有指定@Table注解", clazz.getName());
        }
        return Collections.EMPTY_LIST;
    }

    public <T extends Serializable> List<T> findFromStartToEndRowKey(final Class<T> clazz, String startRowKey, String endRowKey) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz对象不能为空");
        }
        final com.springboot.hbase.annotation.Table table = clazz.getAnnotation(com.springboot.hbase.annotation.Table.class);
        if (table == null) {
            throw new IllegalArgumentException("请检查" + clazz.getName() + "注解@Table是否添加");
        }
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRowKey));
        scan.setStopRow(Bytes.toBytes(endRowKey));

        List<T> list = new ArrayList<>();
        final String columnFamilyName = table.columnFamilyName();
        final String tableName = table.tableName();

        hbaseTemplate.find(tableName, scan, (ResultsExtractor<T>) (ResultScanner results) -> {
            for (Result result : results) {
                final T newInstance = clazz.newInstance();
                byte[] row = result.getRow();
                if(row != null) {
                    parseRowKey(table.generator(), Bytes.toString(row), newInstance);
                }
                findExt(clazz, columnFamilyName, result, newInstance);
                list.add(newInstance);
            }
            return null;
        });
        return list;
    }

    private <T extends Serializable> void findExt(Class<T> clazz, String columnFamilyName, Result result, T newInstance) {
        ReflectionUtils.doWithLocalFields(clazz, field -> {
            fieldPreHandle(field);

            Column column = field.getAnnotation(Column.class);
            byte[] fieldValue;
            HbaseSerializer<?> serializer;
            if(column != null) {
                fieldValue = result.getValue(Bytes.toBytes(columnFamilyName), Bytes.toBytes(column.columnName()));
                serializer =  SpringBeanUtil.getBean(column.serializer());
            } else {
                fieldValue = result.getValue(Bytes.toBytes(columnFamilyName), Bytes.toBytes(field.getName()));
                serializer =  SpringBeanUtil.getBean(StringHbaseSerializer.class);
            }
            // 属性赋值
            if (fieldValue != null) {
                field.set(newInstance, serializer.deserialize(fieldValue));
            }
        });
    }

    public <T> void deleteColumnFamily(String tableName, String columnFamilyName) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            if (!admin.tableExists(tableName)) {
                throw new RuntimeException(tableName + "不存在,请先创建表再删除列簇");
            }

            HTableDescriptor tableDescriptor = admin.getTableDescriptor(TableName.valueOf(tableName));
            HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            String[] columnFamiliesNames = new String[columnFamilies.length];
            for (int i = 0; i < columnFamilies.length; i++) {
                columnFamiliesNames[i] = columnFamilies[i].getNameAsString();
            }
            if (columnFamiliesNames.length <= 1) {
                throw new RuntimeException(tableName + "必须至少有一个ColumnFamily,目前只有" + columnFamiliesNames[0] + "这一个,所以不能删除");
            }
            if (!Arrays.asList(columnFamiliesNames).contains(columnFamilyName)) {
                throw new RuntimeException("columnFamily:" + columnFamilyName + "不存在");
            }

            tableDescriptor.removeFamily(Bytes.toBytes(columnFamilyName));
            admin.disableTable(tableName);
            admin.modifyTable(tableName, tableDescriptor);
            admin.enableTable(tableName);

            // 获取远程元数据的HTableDescriptor对象
            HTableDescriptor tableDescriptorFromMetaData = admin.getTableDescriptor(TableName.valueOf(tableName));
            int count = 0;
            while (true) {
                /**
                 * 比较客户端本地的实例与从元数据获取的实例是否一致(包括所有列簇以及与他们相关的设置)
                 */
                if (tableDescriptor.equals(tableDescriptorFromMetaData)) {
                    log.info("{}的ColumnFamily:[{}]删除成功", tableName, columnFamilyName);
                    break;
                } else {
                    log.warn("{}的ColumnFamily:[{}]没有删除成功,继续循环等待异步返回", tableName, columnFamilyName);
                    try {
                        count++;
                        if (count == 10) {// 等待10秒如果还没有创建删除，就退出
                            throw new RuntimeException(tableName + "{}删除新的ColumnFamily:[" + columnFamilyName + "]没有删除成功");
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    /**
     * 修改列簇前一定要确保表已经被禁用 列簇不能重命名，通常做法是新建一个列簇，然后使用API从旧的列簇中复制数据到新列簇
     */
    public <T> void modifyColumnFamily(String tableName, String columnFamily) {
        HBaseAdmin admin = null;
        try {
            admin = new HBaseAdmin(hbaseTemplate.getConfiguration());
            if (!admin.tableExists(tableName)) {
                throw new RuntimeException(tableName + "不存在,请先创建表再修改列簇");
            }
            admin.disableTable(tableName);
            HTableDescriptor table = admin.getTableDescriptor(TableName.valueOf(tableName));
            HColumnDescriptor existingColumn = new HColumnDescriptor(columnFamily);
            // 使用java提供的或者本地库提供的gzip压缩
            existingColumn.setCompactionCompressionType(Compression.Algorithm.GZ);
            existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            table.modifyFamily(existingColumn);
            admin.modifyTable(tableName, table);
            admin.enableTable(tableName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    public void deleteRow(String tableName, String rowKey, String familyName) {
        hbaseTemplate.delete(tableName, rowKey, familyName);
    }

    private void fieldPreHandle(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new IllegalStateException("@Column注解不被支持在static属性上，因为static无法序列化");
        }
        if (Modifier.isPrivate(modifiers)) {
            field.setAccessible(true);
        }
    }
}
