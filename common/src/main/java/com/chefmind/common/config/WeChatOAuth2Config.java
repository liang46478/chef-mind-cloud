package com.chefmind.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信 OAuth2 登录配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth2.wechat")
public class WeChatOAuth2Config {
    private String appId = "";
    private String appSecret = "";
    private String redirectUri = "http://localhost:5173/login/oauth2/code/wechat";
}
