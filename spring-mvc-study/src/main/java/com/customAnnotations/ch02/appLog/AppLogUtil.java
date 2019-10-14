package com.customAnnotations.ch02.appLog;

import com.alibaba.fastjson.JSONObject;

public final class AppLogUtil {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppLogUtil.class);

    public static void createLog(AppLogType logType, String userId) {
        createLog(logType, userId, false, null, null);
    }

    public static void createLog(AppLogType logType, String userId, boolean guest) {
        createLog(logType, userId, guest, null, null);
    }

    public static void createLog(AppLogType logType, String userId, String key, String value) {
        createLog(logType, userId, false, key, value);
    }

    public static void createLog(AppLogType logType, String userId, boolean guest, String key, String value) {
        JSONObject logInfo = null;
        if (key != null) {
            logInfo = new JSONObject();
            logInfo.put(key, value);
        }
        createLog(logType, userId, guest, logInfo);
    }

    public static void createLog(AppLogType logType, String userId, JSONObject logInfo) {
        createLog(logType, userId, false, logInfo);
    }

    public static void createLog(AppLogType logType, String userId, boolean guest, JSONObject logInfo) {
        AppLog log = new AppLog();
        log.setLogType(logType);
        log.setGuest(guest);
        log.setLogInfo(logInfo);
        log.setLogTime(System.currentTimeMillis());
        log.setUserId(userId);
        //logger.warn(log.toString());
        logger.warn(JSONObject.toJSONString(log));
    }

    public static void main(String[] args) {
        createLog(AppLogType.LOGIN, "2", "testkey", "info test  dsf7688");
    }

}
