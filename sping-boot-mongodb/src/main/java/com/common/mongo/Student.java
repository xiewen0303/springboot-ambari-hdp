package com.common.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "student")
public class Student {
    @Id
    private String id;
    private int age;
    private String name;
    private String star;
    private String like;

    @Field("fimaly_addrs")
    private String fimalyAddrs;

}
