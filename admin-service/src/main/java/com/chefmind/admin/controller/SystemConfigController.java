package com.chefmind.admin.controller;

import com.chefmind.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class SystemConfigController {

    private final JdbcTemplate jdbc;

    @GetMapping("/configs")
    public Result<List<Map<String, Object>>> getConfigs() {
        return Result.success(jdbc.queryForList("SELECT * FROM system_configs ORDER BY id"));
    }

    @PutMapping("/configs")
    public Result<Void> updateConfig(@RequestBody Map<String, String> body) {
        jdbc.update("UPDATE system_configs SET config_value=?, updated_at=NOW() WHERE config_key=?",
                body.get("config_value"), body.get("config_key"));
        return Result.success("更新成功", null);
    }
}
