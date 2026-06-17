package com.chefmind.admin.client;

import com.chefmind.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserServiceClient {

    @GetMapping("/page")
    Result<Map<String, Object>> pageUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword);

    @PutMapping("/{id}/status")
    Result<Void> updateStatus(@PathVariable("id") Long id, @RequestParam String status);
}
