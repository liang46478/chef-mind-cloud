-- A11: 收藏/评论功能
CREATE TABLE IF NOT EXISTS recipe_favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id)
);

CREATE TABLE IF NOT EXISTS recipe_comments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT DEFAULT NULL REFERENCES recipe_comments(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_fav_user ON recipe_favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_fav_recipe ON recipe_favorites(recipe_id);
CREATE INDEX IF NOT EXISTS idx_comments_recipe ON recipe_comments(recipe_id);
