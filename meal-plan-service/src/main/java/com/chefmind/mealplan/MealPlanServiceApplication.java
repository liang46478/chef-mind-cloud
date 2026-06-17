package com.chefmind.mealplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.chefmind.mealplan", "com.chefmind.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class MealPlanServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MealPlanServiceApplication.class, args);
    }
}
