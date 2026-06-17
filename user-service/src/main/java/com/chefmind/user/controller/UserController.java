package com.chefmind.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chefmind.common.result.Result;
import com.chefmind.user.entity.User;
import com.chefmind.user.entity.UserMealRecord;
import com.chefmind.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<User> create(@RequestBody User user) {
        return Result.success("创建成功", userService.createUser(user));
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.success("更新成功", userService.updateUser(user));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        userService.updateStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    @GetMapping("/meal-records")
    public Result<List<UserMealRecord>> getMealRecords(@RequestParam Long userId) {
        return Result.success(userService.getMealRecords(userId));
    }

    @PostMapping("/meal-records")
    public Result<Void> addMealRecord(@RequestBody UserMealRecord record) {
        userService.addMealRecord(record);
        return Result.success("记录成功", null);
    }
}
