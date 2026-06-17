package com.chefmind.recommend.job;

import com.chefmind.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 推荐缓存定时刷新任务
 * 每小时刷新一次推荐缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendRefreshJob {

    private final RecommendService recommendService;

    /**
     * 每小时整点刷新推荐缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshCache() {
        log.info("Starting scheduled recommendation cache refresh...");
        long start = System.currentTimeMillis();
        try {
            recommendService.refreshRecommendationCache();
            long elapsed = System.currentTimeMillis() - start;
            log.info("Recommendation cache refresh completed in {} ms", elapsed);
        } catch (Exception e) {
            log.error("Failed to refresh recommendation cache", e);
        }
    }
}
