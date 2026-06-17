package com.chefmind.recommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.chefmind.recommend", "com.chefmind.common"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class RecommendServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecommendServiceApplication.class, args);
    }
}
