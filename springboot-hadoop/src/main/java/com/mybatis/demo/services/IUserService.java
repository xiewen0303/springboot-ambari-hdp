package com.mybatis.demo.services;

import com.mybatis.demo.model.User;

public interface IUserService {

    public int addUser(User user);

    public Object findAllUser(int pageNum, int pageSize);

    String genMsg(String message);
}
