package com.tron.common.vod.service;

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
import com.tron.common.vod.configuration.VODProperties;
import com.tron.common.vod.dto.UploadVodRespDto;
import com.tron.common.vod.dto.UploadVodieInfo;
import com.tron.common.vod.dto.VodTokenDto;
import com.tron.common.vod.util.ReadFileUtils;
import com.util.Base64Utils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;

@Slf4j
public class VodService  {

    @Resource
    private VODProperties vodProperties;

    /**
     * 获取token
     * @param targetId   是临时Token的会话名称，自己指定用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
    // 但是注意RoleSessionName的长度和规则，不要有空格，只能有'-' '_' 字母和数字等字符
    // 具体规则请参考API文档中的格式要求
     * @param bucketName 权限指定的根目录
     * @param policyPath 权限文件
     * @return
     */
    public VodTokenDto getVodTokenDto(String targetId, String bucketName, String policyPath) {

        String accessKeyId = vodProperties.getAccessKeyId();
        String accessKeySecret = vodProperties.getAccessKeySecret();
        String roleArn = vodProperties.getRoleArn(); // 即角色详情页的Arn值
        String roleSessionName = targetId;//"roleSessionName"; // 自定义即可
        if(null != policyPath){ //设置一个默认的policy文件
            policyPath = "/policy/policy-vod-all.txt";
        }
        String policy = ReadFileUtils.readFile(policyPath);
        ProtocolType protocolType = ProtocolType.HTTPS; // 此处必须为 HTTPS
        try {
            AssumeRoleResponse response = assumeRole(accessKeyId, accessKeySecret, roleArn, roleSessionName, policy, protocolType);
            if(response == null){
                return null;
            }

            VodTokenDto result = new VodTokenDto();
            result.setAccessKeyId(response.getCredentials().getAccessKeyId());
            result.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
            result.setExpiration(response.getCredentials().getExpiration());
            result.setSecurityToken(response.getCredentials().getSecurityToken());
            log.debug("Expiration:{},AccessKeyId:{},AccessKeySecret:{},SecurityToken:{} " , response.getCredentials().getExpiration(),response.getCredentials().getAccessKeyId(),response.getCredentials().getAccessKeySecret(),response.getCredentials().getSecurityToken());
            return result;
        } catch (ClientException e) {
            log.error("Failed to get a token,code:{} \t message:{} " + e.getErrCode(),e.getErrMsg());
        }

        return null;
    }

    private AssumeRoleResponse assumeRole(String accessKeyId, String accessKeySecret,
                                         String roleArn, String roleSessionName, String policy,
                                         ProtocolType protocolType) throws ClientException {
        log.debug("accessKeyId:{},accessKeySecret:{},roleArn:{},roleSessionName:{},policy:{},protocolType:{}",accessKeyId,accessKeySecret,roleArn,roleSessionName,policy,protocolType);
        try {
            // 创建一个 Aliyun Acs Client，用于发起 OpenAPI 请求
            DefaultAcsClient client = initVodClient(accessKeyId,accessKeySecret);

            // 创建一个 AssumeRoleRequest 并设置请求参数
            AssumeRoleRequest request = new AssumeRoleRequest();
            request.setVersion(vodProperties.getStsApiVersion());
//            request.setVersion("2015-04-01");
            request.setSysMethod(MethodType.POST);
            request.setSysProtocol(protocolType);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy);
//            request.setSysLocationProduct(vodProperties.getStorageLocation());
            request.setSysRegionId(vodProperties.getRegion());
            request.setDurationSeconds(vodProperties.getTockenDurationSeconds());
//            request.setDurationSeconds(3600L);
            // 发起请求，并得到response
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("获取vod STS的token异常",e);
        }
        return null;
    }

    public  DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) throws ClientException {
        // 点播服务接入区域，国内请填cn-shanghai，其他区域请参考文档[点播中心](~~98194~~)
//        String regionId =  "cn-shanghai";
        String regionId =  vodProperties.getRegion();
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    private OSSClient initOssClient(JSONObject uploadAuth, JSONObject uploadAddress) {
        String endpoint = uploadAddress.getString("Endpoint");
        String accessKeyId = uploadAuth.getString("AccessKeyId");
        String accessKeySecret = uploadAuth.getString("AccessKeySecret");
        String securityToken = uploadAuth.getString("SecurityToken");
        return new OSSClient(endpoint, accessKeyId, accessKeySecret, securityToken);
    }

    private void uploadLocalFile(OSSClient ossClient, JSONObject uploadAddress, String localFile) throws Exception {
        String bucketName = uploadAddress.getString("Bucket");
        String objectName = uploadAddress.getString("FileName");
        File file = new File(localFile);
        ossClient.putObject(bucketName, objectName, file);
    }

    /**
     * STS 有效期限的参数
     * @param vodTokenDto
     */
    public UploadVodRespDto uploadVodie(VodTokenDto vodTokenDto, UploadVodieInfo uploadVodieInfo) {
        UploadVodRespDto result = new UploadVodRespDto();
        String accessKeyId = vodTokenDto.getAccessKeyId();
        String accessKeySecret = vodTokenDto.getAccessKeySecret();
        String token = vodTokenDto.getSecurityToken();
        String fileName = uploadVodieInfo.getFileName();
        String title = uploadVodieInfo.getTitle();
        String localFile = uploadVodieInfo.getFilePath();

        try {
            CreateUploadVideoResponse createUploadVideoResponse =  createUploadVideo(accessKeyId,accessKeySecret,token,title,fileName);
            String vodieId = createUploadVideoResponse.getVideoId();
            JSONObject uploadAuth = JSONObject.parseObject(new String(Base64Utils.decode(createUploadVideoResponse.getUploadAuth())));
            JSONObject uploadAddress = JSONObject.parseObject(new String(Base64Utils.decode(createUploadVideoResponse.getUploadAddress())));

            // 使用UploadAuth和UploadAddress初始化OSS客户端
            OSSClient ossClient = initOssClient(uploadAuth, uploadAddress);
            // 上传文件，注意是同步上传会阻塞等待，耗时与文件大小和网络上行带宽有关
            uploadLocalFile(ossClient, uploadAddress, localFile);

            result.setVodieId(vodieId);
            result.setUrl(uploadAddress.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取上传文件的信息
     * @param accessKeyId
     * @param accessKeySecret
     * @param token
     * @return
     */
    public CreateUploadVideoResponse createUploadVideo(String accessKeyId, String accessKeySecret, String token,String title,String fileName) {
//        String regionId = "cn-shanghai"; // 点播服务所在的Region，国内请填cn-shanghai，不要填写别的区域
        String regionId = vodProperties.getRegion();
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setSecurityToken(token);
        request.setTitle(title);
        request.setFileName(fileName);
        request.setFileSize(10240L);
        request.setSysEndpoint(vodProperties.getStorageLocation());
        request.setConnectTimeout(1000);
//        request.setStorageLocation(vodProperties.getStorageLocation());
//        request.setStorageLocation("ap-southeast-1");
//        request.setSysProductDomain(new ProductDomain("vod",vodProperties.getStorageLocation()));
//        request.setSysRegionId(vodProperties.getRegion());
        CreateUploadVideoResponse response = null;
        try {
            response = client.getAcsResponse(request);
            log.debug("CreateUploadVideoRequest: {}" , request.getUrl());
            log.debug("CreateUploadVideoRequest, requestId: {}" , response.getRequestId());
            log.debug("UploadAddress： {}" ,response.getUploadAddress());
            log.debug("UploadAuth：{}" , response.getUploadAuth());
            log.debug("VideoId：{}" , response.getVideoId());
        } catch (ClientException e) {
            log.error("action, error:", e);
            e.printStackTrace();
        }
        return response;
    }
}