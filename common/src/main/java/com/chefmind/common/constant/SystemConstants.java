package com.chefmind.common.constant;

/**
 * 系统常量
 */
public interface SystemConstants {

    String SERVICE_NAME_USER = "user-service";
    String SERVICE_NAME_RECIPE = "recipe-service";
    String SERVICE_NAME_MEAL_PLAN = "meal-plan-service";
    String SERVICE_NAME_RECOMMEND = "recommendation-service";
    String SERVICE_NAME_ADMIN = "admin-service";

    String DATE_FORMAT = "yyyy-MM-dd";
    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    int PAGE_SIZE_DEFAULT = 10;
    int PAGE_SIZE_MAX = 100;

    String CACHE_PREFIX_USER = "user:";
    String CACHE_PREFIX_RECIPE = "recipe:";
    String CACHE_PREFIX_MEAL_PLAN = "meal:plan:";
    String CACHE_PREFIX_RECOMMEND = "recommend:";

    long CACHE_TTL_MINUTES = 30;
    long CACHE_TTL_DAY = 24 * 60;
}
