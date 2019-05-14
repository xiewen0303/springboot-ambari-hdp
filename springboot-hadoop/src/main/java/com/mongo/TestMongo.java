package com.mongo;

import com.mongo.Codec.DateCodec;
import com.mongo.bean.Account;
import com.mongo.bean.Address;
import com.mongo.bean.Person;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMongo {

    public void testCounts(){
        CodecRegistry registry = CodecRegistries.fromCodecs(new DateCodec());
        //设置编码解码器
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()),registry);

        //设置MongoClient配置
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry)
                .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("192.168.0.237",27017))))
                .build());
        MongoDatabase mongoDatabase = mongoClient.getDatabase("gamedata");
        MongoCollection<Account>  collections = mongoDatabase.getCollection("account",Account.class);

        Account foundedPerson = collections.find(Filters.eq("serverId", "serverId_121")).first();
        System.out.println(foundedPerson);
    }

    public void testAccount(){

        //设置编码解码器
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

//        Block<ClusterSettings.Builder> block = new Block<ClusterSettings.Builder>() {
//            @Override
//            public void apply(ClusterSettings.Builder builder) {
//                builder.hosts(Arrays.asList(new ServerAddress("192.168.0.237",27017)));
//            }
//        };

//        //设置MongoClient配置
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry)
                .applyToClusterSettings(
//                        block
                      builder -> builder.hosts(Arrays.asList(new ServerAddress("192.168.0.237",27017)))
                )
                .build());

        MongoDatabase mongoDatabase = mongoClient.getDatabase("gamedata");

        MongoCollection<Account> collections = mongoDatabase.getCollection("account",Account.class);

        List<Account> accounts = new ArrayList<>();
        long nowTime = System.currentTimeMillis();
        //保存一个新的记录
        for (int i = 0; i < 1000000; i++ ){
            Account account = new Account();
            account.setJb(600l+i);
            account.setServerId("server6Id_"+i);
            account.setBindYb(6000L+i);
            account.setCreateTime(new Timestamp(nowTime));
            account.setNoReYb(6000L+i);
            account.setUpdateTime(nowTime);
            account.setUserId("wind00"+i);
            account.setUserType(0);
            account.setReYb(6000L+i);
            account.setUserRoleId((long)account.getUserId().hashCode());

            accounts.add(account);
//            collections.insertOne(account);
        }
        collections.insertMany(accounts);
//        long t1 = System.currentTimeMillis();
//
//        Account foundedPerson = collections.find(Filters.eq("serverId", "serverId_323121")).first();
//        System.out.println(System.currentTimeMillis() - t1);
    }

    public void testPoJo(){
        //设置编码解码器
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        //设置MongoClient配置
        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().codecRegistry(pojoCodecRegistry)
                .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress("192.168.0.237",27017))))
                .build());

        //设置数据库
        MongoDatabase testDatabase = mongoClient.getDatabase("test");

        //设置collection名称
        MongoCollection<Person> personColl = testDatabase.getCollection("people", Person.class);

        //清空
        personColl.drop();

        //保存一个新的记录
        Person ada = new Person("jiangjian", 20, new Address("St James Square", "London", "W1"));
        personColl.insertOne(ada);

        //查询
        Person foundedPerson = personColl.find(Filters.and(Filters.eq("name", "jiangjian"), Filters.eq("age", 20))).first();
        System.out.println(foundedPerson);

        //更新
        System.out.println(personColl.updateOne(Filters.eq("name", "jiangjian"), Updates.set("age", 21)));

        //查询结果
        System.out.println(personColl.find(Filters.eq("name", "jiangjian")).first());

        personColl.deleteOne(Filters.eq("name", "jiangjian"));

        System.out.println("删除后，剩余数量为: " + personColl.count());

        mongoClient.close();

    }
}
