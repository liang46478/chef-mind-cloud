-- Admin Service 初始化表结构

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGSERIAL PRIMARY KEY,
    operator VARCHAR(100) DEFAULT NULL,
    action VARCHAR(200) NOT NULL,
    resource_type VARCHAR(50) DEFAULT NULL,
    resource_id BIGINT DEFAULT NULL,
    detail TEXT DEFAULT NULL,
    result VARCHAR(20) DEFAULT 'success',
    duration_ms BIGINT DEFAULT 0,
    ip_address VARCHAR(50) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 推荐策略配置表
CREATE TABLE IF NOT EXISTS recommend_strategy_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_op_logs_operator ON operation_logs(operator);
CREATE INDEX IF NOT EXISTS idx_op_logs_action ON operation_logs(action);
CREATE INDEX IF NOT EXISTS idx_op_logs_created ON operation_logs(created_at);

-- 初始化推荐策略配置
INSERT INTO recommend_strategy_config (config_key, config_value, description) VALUES
('cf_weight', '0.4', '协同过滤权重'),
('cb_weight', '0.3', '基于内容推荐权重'),
('hot_weight', '0.3', '热门推荐权重'),
('enable_cf', 'true', '启用协同过滤'),
('enable_cb', 'true', '启用基于内容推荐'),
('enable_hot', 'true', '启用热门推荐'),
('cold_start_strategy', 'popular', '冷启动策略: popular/random'),
('recommend_limit', '20', '推荐数量上限');
