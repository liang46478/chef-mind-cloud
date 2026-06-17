package com.chefmind.user.controller;

import com.chefmind.common.result.Result;
import com.chefmind.user.entity.User;
import com.chefmind.user.service.UserService;
import com.chefmind.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - 注册/登录
 */
@RestController
@RequestMapping("/api/user/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return Result.error(400, "密码至少6位");
        }

        // 检查用户是否已存在
        User existing = userService.getUserByUsername(request.getUsername());
        if (existing != null) {
            return Result.error(400, "用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus("active");

        User created = userService.createUser(user);

        // 生成 Token
        String token = jwtUtil.generateToken(created.getId(), created.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", created.getId());
        result.put("username", created.getUsername());
        result.put("nickname", created.getNickname());

        return Result.success("注册成功", result);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Result.error(400, "用户名和密码不能为空");
        }

        User user = userService.getUserByUsername(request.getUsername());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        if (!"active".equals(user.getStatus())) {
            return Result.error(403, "账户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());

        return Result.success("登录成功", result);
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) return Result.unauthorized("未登录");

        User user = userService.getUserById(userId);
        if (user == null) return Result.notFound("用户不存在");

        return Result.success(buildProfileMap(user));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody Map<String, Object> body) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) return Result.unauthorized("未登录");

        User user = userService.getUserById(userId);
        if (user == null) return Result.notFound("用户不存在");

        if (body.containsKey("nickname")) user.setNickname((String) body.get("nickname"));
        if (body.containsKey("email")) user.setEmail((String) body.get("email"));
        if (body.containsKey("phone")) user.setPhone((String) body.get("phone"));
        if (body.containsKey("dietaryPreferences")) user.setDietaryPreferences((String) body.get("dietaryPreferences"));
        if (body.containsKey("allergies")) user.setAllergies((String) body.get("allergies"));
        if (body.containsKey("healthGoal")) user.setHealthGoal((String) body.get("healthGoal"));

        userService.updateUser(user);
        return Result.success("更新成功", null);
    }

    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) return null;
        return jwtUtil.getUserIdFromToken(token);
    }

    private Map<String, Object> buildProfileMap(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("nickname", user.getNickname());
        profile.put("avatar", user.getAvatar());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("dietaryPreferences", user.getDietaryPreferences());
        profile.put("allergies", user.getAllergies());
        profile.put("healthGoal", user.getHealthGoal());
        return profile;
    }

    // 请求体类
    static class RegisterRequest {
        private String username;
        private String password;
        private String nickname;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }

    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
