-- Recipe Service 初始化表结构
-- 菜品分类表
CREATE TABLE IF NOT EXISTS recipe_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT NULL REFERENCES recipe_categories(id),
    icon VARCHAR(255) DEFAULT NULL,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 菜谱表
CREATE TABLE IF NOT EXISTS recipes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT DEFAULT NULL,
    image_url VARCHAR(500) DEFAULT NULL,
    prep_time INT DEFAULT 0,
    cook_time INT DEFAULT 0,
    difficulty VARCHAR(20) DEFAULT '初级',
    cuisine_type VARCHAR(50) DEFAULT NULL,
    calories INT DEFAULT 0,
    servings INT DEFAULT 1,
    category_id BIGINT DEFAULT NULL REFERENCES recipe_categories(id),
    status VARCHAR(20) DEFAULT 'draft',
    created_by BIGINT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 食材表
CREATE TABLE IF NOT EXISTS ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(20) NOT NULL DEFAULT '克',
    category VARCHAR(50) DEFAULT NULL,
    calories_per_unit DECIMAL(10,2) DEFAULT 0,
    image_url VARCHAR(500) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 菜谱-食材关联表
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredients(id),
    quantity DECIMAL(10,2) DEFAULT 0,
    unit VARCHAR(20) DEFAULT NULL,
    is_substitutable BOOLEAN DEFAULT false,
    sort_order INT DEFAULT 0
);

-- 菜谱步骤表
CREATE TABLE IF NOT EXISTS recipe_steps (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    image_url VARCHAR(500) DEFAULT NULL,
    timer_minutes INT DEFAULT 0
);

-- 菜谱标签表
CREATE TABLE IF NOT EXISTS recipe_tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 菜谱-标签关联表
CREATE TABLE IF NOT EXISTS recipe_tag_mapping (
    id BIGSERIAL PRIMARY KEY,
    recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES recipe_tags(id) ON DELETE CASCADE,
    UNIQUE(recipe_id, tag_id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_recipes_category ON recipes(category_id);
CREATE INDEX IF NOT EXISTS idx_recipes_status ON recipes(status);
CREATE INDEX IF NOT EXISTS idx_recipes_cuisine ON recipes(cuisine_type);
CREATE INDEX IF NOT EXISTS idx_recipes_created_by ON recipes(created_by);
CREATE INDEX IF NOT EXISTS idx_recipe_steps_recipe ON recipe_steps(recipe_id);
CREATE INDEX IF NOT EXISTS idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id);

-- 初始分类数据
INSERT INTO recipe_categories (name, icon, sort_order) VALUES
('川菜', 'pi pi-star', 1),
('粤菜', 'pi pi-star', 2),
('鲁菜', 'pi pi-star', 3),
('苏菜', 'pi pi-star', 4),
('湘菜', 'pi pi-star', 5),
('徽菜', 'pi pi-star', 6),
('浙菜', 'pi pi-star', 7),
('闽菜', 'pi pi-star', 8),
('家常菜', 'pi pi-home', 9),
('汤羹', 'pi pi-heart', 10),
('凉菜', 'pi pi-snowflake', 11),
('主食', 'pi pi-bolt', 12),
('甜品', 'pi pi-heart-fill', 13);
