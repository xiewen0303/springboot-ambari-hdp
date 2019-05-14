package com.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;


public class HadoopUtil {

    static Logger logger = Logger.getLogger(HadoopUtil.class);

    private String path;
    private String userName;
    private static String hdfsPath = "hdfs://192.168.0.237:9000";
    private static String hdfsName = "root";

    /**
     * 获取hdfs文件系统对象
     * @return
     */
    private static Configuration getConfiguration(){
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS",hdfsPath);
        return configuration;
    }

    /**
     * 获取hdfs文件系统对象
     * @return
     * @throws Exception
     */
    public static FileSystem getFileSystem() throws Exception{
        FileSystem fileSystem = FileSystem.get(new URI(hdfsPath),getConfiguration(),hdfsName);
        return fileSystem;
    }

    public static void mkdir(String path) throws Exception{
        FileSystem fs = HadoopUtil.getFileSystem();
        Path newPath = new Path(path);
        boolean isOk = fs.mkdirs(newPath);
        fs.close();
        logger.debug("mkdir status:"+isOk);
    }



    public static void createFile(String path,File file) throws Exception {

        FileSystem fileSystem = HadoopUtil.getFileSystem();

        String fileName = file.getName();
        Path newPath = new Path(path +"/"+fileName);
        FSDataOutputStream outputStream = fileSystem.create(newPath);

        FileInputStream fis = new FileInputStream(file);
        int len = fis.available();
        if(len > 0){
            byte[] fileData = new byte[len];
            fis.read(fileData);

            outputStream.write(fileData);
        }

        fis.close();
        outputStream.flush();
        outputStream.close();
    }


    //读取文件
    public static void readFile(String path) throws Exception{
        FileSystem fileSystem = HadoopUtil.getFileSystem();
        Path newPath = new Path(path);
        InputStream in = fileSystem.open(newPath);

        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String content = null;
        while((content = br.readLine()) != null){
            System.out.println(content);
        }

        in.close();
        br.close();
    }

    public  static void readPathInfo(String path) throws Exception {
        FileSystem fileSystem = HadoopUtil.getFileSystem();

        FileStatus[] status = fileSystem.listStatus(new Path(path));
        for (FileStatus s : status) {
            logger.info(s.getPath()+"_"+s.toString());
        }
    }

    /**
     * 删除文件
     * @param path
     * @throws Exception
     */
    public  static void deleteFile(String path) throws Exception {
        FileSystem fileSystem = HadoopUtil.getFileSystem();

        boolean flag = fileSystem.deleteOnExit(new Path(path));
        logger.info("flag===="+flag);
    }


    public static void main(String[] args) throws  Exception {
         mkdir("demo4");

         createFile("demo1",new File("G:\\2.txt"));

//         readFile("demo1/2.txt");

    //     readPathInfo("demo1");

//        deleteFile("demo1/1.txt");
    }
}
