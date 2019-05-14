package com.mybatis.demo.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mybatis.demo.mapper.UserMapper;
import com.mybatis.demo.model.User;
import com.mybatis.demo.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    public int addUser(User user) {
        return userMapper.insert(user);
    }

    public Object findAllUser(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return userMapper.selectAll();
    }

    @Override
    public String genMsg(String message) {
        System.out.println("=========="+message);
        return null;
    }
}
