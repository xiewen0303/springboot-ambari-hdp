package com.customAnnotations.ch02.appLog;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class AppLog {

    private AppLogType logType;

    private String userId;

    private boolean guest;//是否是游客 如果不是 userId就是用户id  如果是 userId可以是其他的一个唯一标识

    private JSONObject logInfo;//不同的log包含的不同信息

    private Long logTime;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String spiltStart = "=";
        String spiltEnd = "&";
        sb.append("type").append(spiltStart).append(logType.getDetail()).append(spiltEnd);
        sb.append("userId").append(spiltStart).append(userId).append(spiltEnd);
        int guestFlag = guest ? 1 : 0;
        sb.append("guest").append(spiltStart).append(guestFlag).append(spiltEnd);
        if (logInfo != null) {
            String info = logInfo.toString().replace("=", "{1}").replace("&", "{2}");
            sb.append("info").append(spiltStart).append(info).append(spiltEnd);
        }
        sb.append("logTime").append(spiltStart).append(logTime);
        return sb.toString();
    }

}
