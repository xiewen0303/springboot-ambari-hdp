package com.tron.common.vod.configuration;

import lombok.Data;


@Data
public class VODProperties {

    private String accessKeyId;
    private String accessKeySecret;
    private String roleArn;
    private String roleSessionName;
    private String region;          //cn-hangzhou
    private String policyPath;
    private String stsApiVersion; //2015-04-01
    private Long tockenDurationSeconds; //tocken的有效期,单位秒
    private String storageLocation; //区域指定
}