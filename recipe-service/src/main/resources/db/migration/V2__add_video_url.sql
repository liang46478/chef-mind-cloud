-- Recipe Service V2: 添加视频链接支持
ALTER TABLE recipes ADD COLUMN IF NOT EXISTS video_url VARCHAR(500) DEFAULT NULL;
ALTER TABLE recipe_steps ADD COLUMN IF NOT EXISTS video_url VARCHAR(500) DEFAULT NULL;

COMMENT ON COLUMN recipes.video_url IS '做菜视频链接（支持B站/抖音/YouTube等平台）';
COMMENT ON COLUMN recipe_steps.video_url IS '步骤相关视频链接';
