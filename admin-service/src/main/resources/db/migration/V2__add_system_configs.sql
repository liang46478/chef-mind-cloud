-- B8: 系统配置表
CREATE TABLE IF NOT EXISTS system_configs (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_configs (config_key, config_value, description) VALUES
('site_name', 'ChefMind', '站点名称'),
('site_description', '智能餐食规划系统', '站点描述'),
('max_meal_plan_days', '7', '最大计划天数'),
('default_cuisine', '家常菜', '默认菜系'),
('enable_registration', 'true', '是否开放注册'),
('require_email_verify', 'false', '是否需要邮箱验证')
ON CONFLICT (config_key) DO NOTHING;
