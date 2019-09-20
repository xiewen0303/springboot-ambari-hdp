package com.oss.voddemo;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OtherSTS {

    public static void main(String[] args) throws Exception {
        //RAM子账号
//        String accessKeyId = "LTAI4FwqD1rmfbK67HnhutcJ";
//        String accessKeySecret = "4Ul0kT5vdL5TcuV2zhZH8A6m2Ke86v";

        //主账号
        String accessKeyId = "LTAI4FgZ5r4U7CBFaG6uQxiQ";
        String accessKeySecret = "sz6R28JlKJ3yoS5yHzUJmC9PfFoA9i";

        DefaultAcsClient defaultAcsClient = initVodClient(accessKeyId,accessKeySecret);
        CreateUploadVideoResponse response =  createUploadVideo(defaultAcsClient);
        System.out.println(JSONObject.toJSONString(response));
    }


    private static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) throws ClientException {
        // 点播服务接入区域，国内请填cn-shanghai，其他区域请参考文档[点播中心](~~98194~~)
        String regionId =  "ap-southeast-1";// TODO vodProperties.getRegion();
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    public static CreateUploadVideoResponse createUploadVideo(DefaultAcsClient vodClient) throws Exception {
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setFileName("D:\\tmp\\test1.mp4");
        request.setTitle("this is title");
        request.setSysEndpoint("vod.ap-southeast-1.aliyuncs.com");
        //request.setDescription("this is desc");
        //request.setTags("tag1,tag2");
        //request.setCoverURL("http://vod.aliyun.com/test_cover_url.jpg");
        //request.setCateId(-1L);
        //request.setTemplateGroupId("");
        //request.setWorkflowId("");
        //request.setStorageLocation("");
        //request.setAppId("app-1000000");
        //设置请求超时时间
        request.setSysReadTimeout(1000);
        request.setSysConnectTimeout(1000);
        return vodClient.getAcsResponse(request);
    }
}
