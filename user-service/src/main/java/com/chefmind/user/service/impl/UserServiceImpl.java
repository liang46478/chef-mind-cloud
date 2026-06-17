package com.chefmind.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chefmind.user.entity.User;
import com.chefmind.user.entity.UserMealRecord;
import com.chefmind.user.mapper.UserMapper;
import com.chefmind.user.mapper.UserMealRecordMapper;
import com.chefmind.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserMealRecordMapper mealRecordMapper;

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public IPage<User> pageUsers(Page<User> page, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(keyword != null && !keyword.trim().isEmpty(), User::getUsername, keyword)
                .or(keyword != null && !keyword.trim().isEmpty(),
                    w -> w.like(User::getNickname, keyword))
                .orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public List<UserMealRecord> getMealRecords(Long userId) {
        return mealRecordMapper.selectList(
                new LambdaQueryWrapper<UserMealRecord>()
                        .eq(UserMealRecord::getUserId, userId)
                        .orderByDesc(UserMealRecord::getAteAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMealRecord(UserMealRecord record) {
        mealRecordMapper.insert(record);
    }
}
