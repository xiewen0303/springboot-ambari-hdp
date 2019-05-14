package com.mongo;

import com.mongo.Codec.DateCodec;
import com.mongo.bean.Account;
import com.mongo.bean.Address;
import com.mongo.bean.Person;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;


public class MongoService {

    public static MongoClient mongoClient;

//    public static void main(String[] args){
//        intiMongoClient("192.168.0.237",27017);
//        MongoDatabase mongoDatabase = getMongoDataBase("calon");
//
////        testShowTables();
////        testDatas(mongoDatabase);
//
//        //添加数据
//        insertObject(mongoDatabase);
//    }

    public static void testDatas( MongoDatabase mongoDatabase){

        MongoCollection<Document>  collections = mongoDatabase.getCollection("user");
        System.out.println(collections.count());

//        查询所有的
//        MongoCursor<Document> sd = collections.find().iterator();
//        while(sd.hasNext()){
//            Document document = sd.next();
//            System.out.println(document.toJson());
//        }



//////      依据字段查询
//        Iterator<Document> datas =  collections.find(Filters.eq("name","wind666")).iterator();
//        while(datas.hasNext()){
//            Document document = datas.next();
//            System.out.println(document.toJson());
//            break;
//        }


////        根据Id查询
//        FindIterable<Document>  findIterable = collections.find(Filters.eq("id",1));
//        Document firstDocument = findIterable.first();
//        System.out.println(firstDocument.toJson());




//        //删除一个表
//        collections.drop();

//        //删除数据库
//        mongoDatabase.drop();

////        更新一条数据
//        updateObject(collections);




    }

    private static void insertObject(MongoDatabase mongoDatabase) {
        MongoCollection<Account> collections = mongoDatabase.getCollection("account",Account.class);

        //保存一个新的记录
        Account account = new Account();
        account.setJb(100l);
        account.setServerId("serverId_121212");
        collections.insertOne(account);
    }

    private static void updateObject(MongoCollection<Document> collections) {
        // 修改数据
        String id = "5c3c79a68169c5a98dc21d09";
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId("5c3c79a68169c5a98dc21d09");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bson filter = Filters.eq("_id",_idobj);

        Document beginData = collections.find(filter).first();
        System.out.println("begin====:" +beginData.toString());


        Document newdoc = new Document();
        newdoc.put("name", "谢稳");
        collections.updateOne(filter,new Document("$set",newdoc));


        Document endData = collections.find(filter).first();
        System.out.println("end====:" +endData.toString());
    }

    /**
     * 展示所有表名
     */
    public static void testShowTables() {

        MongoDatabase mongoDatabase = getMongoDataBase("calon");
        MongoIterable<String> colls = mongoDatabase.listCollectionNames();
        for (String msg : colls) {
            System.out.println(msg);
        }
    }

    /**
     * 显示所有数据库名字
     */
    public static void testShowDataBases(){
        MongoIterable<String> mongoIterable = getAllDBNames();
        for (String data : mongoIterable) {
            System.out.println(data);
        }
    }

    public static void intiMongoClient(String ip,int port){


        MongoClientOptions.Builder options = new MongoClientOptions.Builder();

        //是否创建了一个finalize方法，用于清除客户端未关闭的DBCursor实例。
        options.cursorFinalizerEnabled(true);

        //此MongoClient实例的每个主机允许的最大连接数。这些连接在空闲时将保存在池中。池耗尽后，任何需要连接的操作都将阻止等待可用连接。 默认值为100
        options.connectionsPerHost(300);

        //连接超时（以毫秒为单位）
        options.connectTimeout(30000);

        //线程可能等待连接变为可用的最长等待时间（以毫秒为单位）
        options.maxWaitTime(5000);

        //套接字超时时间，0无限制
        options.socketTimeout(0);


        // 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.threadsAllowedToBlockForConnectionMultiplier(5000);
        options.writeConcern(WriteConcern.SAFE);

        mongoClient = new MongoClient(new ServerAddress(ip,port),options.build());

    }

    public static MongoClientOptions getOptions(){
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();

        //是否创建了一个finalize方法，用于清除客户端未关闭的DBCursor实例。
        options.cursorFinalizerEnabled(true);

        //此MongoClient实例的每个主机允许的最大连接数。这些连接在空闲时将保存在池中。池耗尽后，任何需要连接的操作都将阻止等待可用连接。 默认值为100
        options.connectionsPerHost(300);

        //连接超时（以毫秒为单位）
        options.connectTimeout(30000);

        //线程可能等待连接变为可用的最长等待时间（以毫秒为单位）
        options.maxWaitTime(5000);

        //套接字超时时间，0无限制
        options.socketTimeout(0);

        CodecRegistry registry = CodecRegistries.fromCodecs(new DateCodec());
        options.codecRegistry(registry);

        // 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.threadsAllowedToBlockForConnectionMultiplier(5000);
        options.writeConcern(WriteConcern.SAFE);
        return  options.build();
    }



    public static MongoIterable<String> getAllDBNames(){
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }


    public static MongoDatabase getMongoDataBase(String dbName) {
        if(dbName != null && !"".equals(dbName)){
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }
}