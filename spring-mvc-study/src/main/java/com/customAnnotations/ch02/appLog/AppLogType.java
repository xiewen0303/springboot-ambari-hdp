package com.customAnnotations.ch02.appLog;

public enum AppLogType {

    LOGIN(101, "登录"),
    LOGOUT(102, "登出"),
    CHANGE_PWD(103, "修改密码"),
    REGISTER(104, "注册");

    private int type;

    private String detail;

    AppLogType(int type, String detail) {
        this.type = type;
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public int getType() {
        return type;
    }

    public static String getDetailByType(int type) {
        for (AppLogType logType : values()) {
            if (logType.getType() == type) {
                return logType.getDetail();
            }
        }
        return null;
    }

}
