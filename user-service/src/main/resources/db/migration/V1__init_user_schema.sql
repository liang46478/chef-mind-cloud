-- User Service 初始化表结构

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) DEFAULT NULL,
    avatar VARCHAR(500) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL UNIQUE,
    phone VARCHAR(20) DEFAULT NULL,
    dietary_preferences TEXT DEFAULT NULL,
    allergies TEXT DEFAULT NULL,
    health_goal VARCHAR(100) DEFAULT '均衡营养',
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户饮食偏好表
CREATE TABLE IF NOT EXISTS user_dietary_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    cuisine_type VARCHAR(50) DEFAULT NULL,
    taste VARCHAR(100) DEFAULT NULL,
    spiciness_level VARCHAR(20) DEFAULT '微辣',
    meal_type VARCHAR(20) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户就餐记录表
CREATE TABLE IF NOT EXISTS user_meal_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id BIGINT DEFAULT NULL,
    recipe_name VARCHAR(200) DEFAULT NULL,
    meal_type VARCHAR(20) NOT NULL,
    rating INT DEFAULT 0,
    feedback TEXT DEFAULT NULL,
    ate_at DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_records_user ON user_meal_records(user_id);
CREATE INDEX IF NOT EXISTS idx_user_records_date ON user_meal_records(ate_at);
