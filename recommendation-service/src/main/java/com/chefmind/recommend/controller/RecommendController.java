package com.chefmind.recommend.controller;

import com.chefmind.common.result.Result;
import com.chefmind.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/recipes")
    public Result<List<Long>> getRecommendations(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "20") int topN) {
        return Result.success(recommendService.getRecommendations(userId, topN));
    }

    @GetMapping("/cf")
    public Result<List<Long>> getUserCf(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "20") int topN) {
        return Result.success(recommendService.getUserCfRecommendations(userId, topN));
    }

    @GetMapping("/item-cf")
    public Result<List<Long>> getItemCf(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "20") int topN) {
        return Result.success(recommendService.getItemCfRecommendations(userId, topN));
    }

    @GetMapping("/hot")
    public Result<List<Long>> getHot(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "20") int topN) {
        return Result.success(recommendService.getHotRecommendations(userId, topN));
    }

    @GetMapping("/content-based")
    public Result<List<Long>> getContentBased(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "20") int topN) {
        return Result.success(recommendService.getContentBasedRecommendations(userId, topN));
    }

    @PostMapping("/interaction")
    public Result<Void> recordInteraction(
            @RequestParam Long userId,
            @RequestParam Long recipeId,
            @RequestParam String type,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer duration) {
        recommendService.recordInteraction(userId, recipeId, type, rating, duration);
        return Result.success("记录成功", null);
    }
}
