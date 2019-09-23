package com.tron.common.vod.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class ReadFileUtils {

    /**
     * 读取配置文件
     * @param path
     * @return
     */
    public static String readFile(String path) {
//        BufferedReader reader = null;
        StringBuilder data = new StringBuilder();
//        //
//        try {
//            ClassPathResource resource = new ClassPathResource(path);
//            if(!resource.exists()){
//                log.error("读取文件地址不正确，path={}",path);
//                return null;
//            }
//            InputStream inputStream = resource.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
//            String temp = null;
//            while ((temp = reader.readLine()) != null) {
//                data.append(temp);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    log.error("【读取VOD policy配置文件】关闭流异常");
//                }
//            }
//        }
////        String dataStr = data.toString();
////        String bucketName = OSSBucketHolder.get("cocokvip");
////        return dataStr.replaceAll("\\$BUCKET_NAME", bucketName);

        return data.toString();
    }
}
