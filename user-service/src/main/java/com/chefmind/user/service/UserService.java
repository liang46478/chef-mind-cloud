package com.chefmind.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.user.entity.User;
import com.chefmind.user.entity.UserMealRecord;

import java.util.List;

public interface UserService {

    User getUserById(Long id);

    User getUserByUsername(String username);

    User createUser(User user);

    User updateUser(User user);

    void updateStatus(Long id, String status);

    IPage<User> pageUsers(Page<User> page, String keyword);

    List<UserMealRecord> getMealRecords(Long userId);

    void addMealRecord(UserMealRecord record);
}
