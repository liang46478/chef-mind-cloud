package com.chefmind.recipe.controller;

import com.chefmind.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir, "images"));
        } catch (IOException e) {
            // ignore
        }
    }

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + ext;

        try {
            Path targetPath = Paths.get(uploadDir, "images", fileName);
            Files.copy(file.getInputStream(), targetPath);
            String url = "/api/upload/images/" + fileName;
            return Result.success("上传成功", url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/images/{fileName}")
    public byte[] getImage(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(uploadDir, "images", fileName);
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        }
        throw new IOException("文件不存在");
    }
}
