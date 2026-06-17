-- Recommendation Service 初始化表结构

-- 用户-菜品交互表（隐式反馈）
CREATE TABLE IF NOT EXISTS user_recipe_interactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    interaction_type VARCHAR(30) NOT NULL, -- view, favorite, rate, cook, complete
    rating INT DEFAULT 0,
    duration_seconds INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 推荐日志表
CREATE TABLE IF NOT EXISTS recommendation_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recommended_recipe_ids TEXT DEFAULT NULL,
    strategy VARCHAR(50) DEFAULT NULL, -- cf, content-based, hot, hybrid
    click_through BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户相似度表（缓存 User-CF 结果）
CREATE TABLE IF NOT EXISTS user_similarity_cache (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    similar_user_id BIGINT NOT NULL,
    similarity_score DECIMAL(10,6) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, similar_user_id)
);

-- 菜品相似度表（缓存 Item-CF 结果）
CREATE TABLE IF NOT EXISTS recipe_similarity_cache (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL,
    similar_recipe_id BIGINT NOT NULL,
    similarity_score DECIMAL(10,6) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(recipe_id, similar_recipe_id)
);

-- 推荐结果缓存表
CREATE TABLE IF NOT EXISTS recommendation_cache (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_ids TEXT NOT NULL,
    strategy VARCHAR(50) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, strategy)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_interactions_user ON user_recipe_interactions(user_id);
CREATE INDEX IF NOT EXISTS idx_interactions_recipe ON user_recipe_interactions(recipe_id);
CREATE INDEX IF NOT EXISTS idx_interactions_type ON user_recipe_interactions(interaction_type);
CREATE INDEX IF NOT EXISTS idx_rec_cache_user ON recommendation_cache(user_id);
