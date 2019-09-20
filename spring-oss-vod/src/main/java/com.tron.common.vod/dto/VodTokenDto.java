package com.tron.common.vod.dto;

import lombok.Data;

@Data
public class VodTokenDto {

    private String expiration;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

}
