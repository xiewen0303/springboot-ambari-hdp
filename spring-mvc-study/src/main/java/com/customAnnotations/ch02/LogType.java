package com.customAnnotations.ch02;

public enum LogType {

    LOGIN(1, "登录"),
    LOGOUT(2, "登出"),
    CHANGE_PWD(3, "修改本地管理员密码"),

    ACCOUNT_TRANSFER(101, "转账"),
    ACCOUNT_ADD(102, "添加转账账号"),
    ACCOUNT_EDIT(103, "编辑转账账号"),
    ACCOUNT_PWD(104, "转账账号密码修改"),
    ACCOUNT_FORBID(105, "禁用转账账号"),
    ACCOUNT_ENABLE(106, "启用转账账号"),

    ADMIN_ADD(201, "添加管理员"),
    ADMIN_EDIT(202, "编辑管理员"),
    ADMIN_PWD(203, "管理员密码修改"),
    ADMIN_FORBID(204, "禁用管理员"),
    ADMIN_ENABLE(205, "启用管理员"),

    ROLE_ADD(301, "添加角色"),
    ROLE_EDIT(302, "编辑角色"),
    ROLE_AUTHORIZE(303, "授权角色"),

    PERMISSION_ADD(401, "添加权限"),
    PERMISSION_EDIT(402, "编辑权限"),
    PERMISSION_DELETE(403, "删除权限");

    private int type;

    private String detail;

    LogType(int type, String detail) {
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
        for (LogType logType : values()) {
            if (logType.getType() == type) {
                return logType.getDetail();
            }
        }
        return null;
    }
}
