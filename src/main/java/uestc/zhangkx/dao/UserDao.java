package uestc.zhangkx.dao;


import uestc.zhangkx.domain.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();
}
