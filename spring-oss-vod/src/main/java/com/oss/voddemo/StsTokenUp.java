package com.oss.voddemo;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.util.Base64Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class StsTokenUp {


    private AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret,
                                          String roleArn, String roleSessionName, String policy,
                                          ProtocolType protocolType) throws ClientException {
        log.debug("accessKeyId:{},accessKeySecret:{},roleArn:{},roleSessionName:{},policy:{},protocolType:{}",accessKeyId,accessKeySecret,roleArn,roleSessionName,policy,protocolType);
        try {
            // 创建一个 Aliyun Acs Client，用于发起 OpenAPI 请求
            DefaultAcsClient client = initVodClient(accessKeyId,accessKeySecret);

            // 创建一个 AssumeRoleRequest 并设置请求参数
            AssumeRoleRequest request = new AssumeRoleRequest();
//           TODO request.setVersion(vodProperties.getStsApiVersion());
            request.setVersion("2015-04-01");
            request.setMethod(MethodType.POST);
            request.setProtocol(protocolType);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
//           TODO request.setDurationSeconds(vodProperties.getTockenDurationSeconds());
            request.setDurationSeconds(3600L);
            // 发起请求，并得到response
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("获取vod STS的token异常",e);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        DefaultAcsClient defaultAcsClient =  initVodClient("LTAI4FwqD1rmfbK67HnhutcJ","4Ul0kT5vdL5TcuV2zhZH8A6m2Ke86v");

        CreateUploadVideoResponse response = createUploadVideo(defaultAcsClient);
        System.out.println(response);

    }

    private static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) throws ClientException {
        // 点播服务接入区域，国内请填cn-shanghai，其他区域请参考文档[点播中心](~~98194~~)
        String regionId =  "cn-shanghai";// TODO vodProperties.getRegion();
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }



    public static CreateUploadVideoResponse createUploadVideo(DefaultAcsClient vodClient) throws Exception {
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setFileName("D:\\tmp\\test1.mp4");
        request.setTitle("this is title");
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

    public static OSSClient initOssClient(JSONObject uploadAuth, JSONObject uploadAddress) {
        String endpoint = uploadAddress.getString("Endpoint");
        String accessKeyId = uploadAuth.getString("AccessKeyId");
        String accessKeySecret = uploadAuth.getString("AccessKeySecret");
        String securityToken = uploadAuth.getString("SecurityToken");
        return new OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken);
    }

    public static void uploadLocalFile(OSSClient ossClient, JSONObject uploadAddress, String localFile) throws Exception {
        String bucketName = uploadAddress.getString("Bucket");
        String objectName = uploadAddress.getString("FileName");
        File file = new File(localFile);
        ossClient.putObject(bucketName, objectName, file);
    }

    public AssumeRoleResponse getAssumeRoleResponse() {
        AssumeRoleResponse response = null;
        String accessKeyId = "LTAI4FwqD1rmfbK67HnhutcJ";
        String accessKeySecret = "4Ul0kT5vdL5TcuV2zhZH8A6m2Ke86v";
        String roleArn = "acs:ram::1476600307105402:role/vodrole"; // 即角色详情页的Arn值
        String roleSessionName = "roleSessionName"; // 自定义即可
        // 定制你的policy
        String policy = "{\n" +
                "  \"Version\": \"1\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Action\": \"vod:*\",\n" +
                "      \"Resource\": \"*\",\n" +
                "      \"Effect\": \"Allow\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        // 此处必须为 HTTPS
        ProtocolType protocolType = ProtocolType.HTTPS;
        try {
            response =  assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, protocolType);
            System.out.println("Expiration: " + response.getCredentials().getExpiration());
            System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
            System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
            System.out.println("Security Token: " + response.getCredentials().getSecurityToken());

        } catch (ClientException e) {
            System.out.println("Failed to get a token.");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
        }
        return response;
    }

    public CreateUploadVideoResponse createUploadVideo(String accessKeyId, String accessKeySecret, String token) {
        String regionId = "cn-shanghai"; // 点播服务所在的Region，国内请填cn-shanghai，不要填写别的区域
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setSecurityToken(token);
        request.setTitle("t1");
        request.setFileName("D:\\tmp\\test1.mp4");
        request.setFileSize(10240L);
        request.setConnectTimeout(1000);
        CreateUploadVideoResponse response = null;
        try {
            response = client.getAcsResponse(request);
            System.out.println("CreateUploadVideoRequest：" + request.getUrl());
            System.out.println("CreateUploadVideoRequest, requestId:" + response.getRequestId());
            System.out.println("UploadAddress： " + response.getUploadAddress());
            System.out.println("UploadAuth：" + response.getUploadAuth());
            System.out.println("VideoId：" + response.getVideoId());
        } catch (ClientException e) {
            System.out.println("action, error:" + e);
            e.printStackTrace();
        }
        return response;
    }



    public static void main2(String[] args) throws Exception {

        // 初始化VOD客户端并获取上传地址和凭证
        StsTokenUp vodService = new StsTokenUp();
//        String accessKeyId ="LTAI4FwqD1rmfbK67HnhutcJ";
//        String accessKeySecret = "4Ul0kT5vdL5TcuV2zhZH8A6m2Ke86v";
//        DefaultAcsClient vodClient = vodService.initVodClient(accessKeyId, accessKeySecret);
//        CreateUploadVideoResponse createUploadVideoResponse = createUploadVideo(vodClient);

        AssumeRoleResponse response = vodService.getAssumeRoleResponse();
        CreateUploadVideoResponse createUploadVideoResponse =  vodService.createUploadVideo(response.getCredentials().getAccessKeyId(),
                response.getCredentials().getAccessKeySecret(),
                response.getCredentials().getSecurityToken());

        // 执行成功会返回VideoId、UploadAddress和UploadAuth
        String videoId = createUploadVideoResponse.getVideoId();
        JSONObject uploadAuth = JSONObject.parseObject(new String(Base64Utils.decode(createUploadVideoResponse.getUploadAuth())));
        JSONObject uploadAddress = JSONObject.parseObject(new String(Base64Utils.decode(createUploadVideoResponse.getUploadAddress())));

        // 使用UploadAuth和UploadAddress初始化OSS客户端
        OSSClient ossClient = initOssClient(uploadAuth, uploadAddress);
        // 上传文件，注意是同步上传会阻塞等待，耗时与文件大小和网络上行带宽有关
        String localFile="D:\\tmp\\test1.mp4";
        uploadLocalFile(ossClient, uploadAddress, localFile);
        System.out.println(videoId);
    }
}
