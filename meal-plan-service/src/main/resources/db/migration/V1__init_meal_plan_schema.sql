-- Meal Plan Service 初始化表结构

-- 用餐计划表
CREATE TABLE IF NOT EXISTS meal_plans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL DEFAULT '',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    plan_type VARCHAR(20) NOT NULL DEFAULT 'weekly',
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用餐计划详情表
CREATE TABLE IF NOT EXISTS meal_plan_items (
    id BIGSERIAL PRIMARY KEY,
    meal_plan_id BIGINT NOT NULL REFERENCES meal_plans(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    recipe_id BIGINT DEFAULT NULL,
    recipe_name VARCHAR(200) DEFAULT NULL,
    sort_order INT DEFAULT 0,
    notes TEXT DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI 餐食生成日志表
CREATE TABLE IF NOT EXISTS ai_meal_generation_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT DEFAULT NULL,
    prompt TEXT DEFAULT NULL,
    response TEXT DEFAULT NULL,
    model_used VARCHAR(100) DEFAULT NULL,
    tokens_used INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 提示词配置表
CREATE TABLE IF NOT EXISTS prompt_configs (
    id BIGSERIAL PRIMARY KEY,
    prompt_type VARCHAR(50) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    variables VARCHAR(500) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_meal_plans_user ON meal_plans(user_id);
CREATE INDEX IF NOT EXISTS idx_meal_plans_dates ON meal_plans(start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_meal_plan_items_plan ON meal_plan_items(meal_plan_id);
CREATE INDEX IF NOT EXISTS idx_meal_plan_items_date ON meal_plan_items(date);

-- 初始化默认提示词
INSERT INTO prompt_configs (prompt_type, content, description, variables) VALUES
('meal-plan', '你是一个专业的营养师和厨师。请根据用户的以下信息生成一周的餐食计划：\n- 口味偏好：{cuisine_preference}\n- 忌口/过敏源：{allergens}\n- 健康目标：{health_goal}\n\n请生成一日三餐的详细安排，包括菜品名称、简要做法和营养说明。', 'AI 用餐计划生成提示词', 'cuisine_preference, allergens, health_goal'),
('recipe', '请根据以下信息生成详细的菜谱：\n- 菜名：{meal}\n- 可用食材：{ingredients}\n- 烹饪时间：{cooking_time}\n\n要求包含食材清单、烹饪步骤、所需时间和火力建议。', 'AI 菜谱生成提示词', 'meal, ingredients, cooking_time');
