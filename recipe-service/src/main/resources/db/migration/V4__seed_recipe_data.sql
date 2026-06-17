-- Recipe Service V4: 种子数据 - 20道常见菜品
CREATE UNIQUE INDEX IF NOT EXISTS idx_ingredients_name ON ingredients(name);

-- 食材 (36种)
INSERT INTO ingredients (name, unit, category, calories_per_unit) VALUES
('鸡胸肉','克','肉类',167),('花生米','克','干货',567),('干辣椒','个','调味料',0),
('花椒','克','调味料',0),('葱','根','蔬菜',0),('姜','块','蔬菜',0),
('蒜','瓣','蔬菜',0),('番茄','个','蔬菜',18),('鸡蛋','个','蛋类',144),
('鲈鱼','条','水产',105),('豆腐','块','豆制品',81),('猪肉','克','肉类',395),
('土豆','个','蔬菜',77),('青椒','个','蔬菜',0),('牛肉','克','肉类',250),
('茄子','个','蔬菜',25),('黄瓜','根','蔬菜',15),('虾仁','克','水产',93),
('排骨','克','肉类',264),('白菜','颗','蔬菜',13),('面粉','克','主食',366),
('大米','克','主食',130),('生抽','毫升','调味料',0),('料酒','毫升','调味料',0),
('淀粉','克','调味料',0),('白糖','克','调味料',0),('醋','毫升','调味料',0),
('豆瓣酱','克','调味料',0),('香菇','个','蔬菜',0),('胡萝卜','根','蔬菜',0),
('木耳','克','干货',0),('洋葱','个','蔬菜',0),('面条','克','主食',284),
('五花肉','克','肉类',395),('豆角','克','蔬菜',31)
ON CONFLICT DO NOTHING;

CREATE OR REPLACE FUNCTION get_ing_id(n TEXT) RETURNS BIGINT AS $$ SELECT id FROM ingredients WHERE name=n LIMIT 1; $$ LANGUAGE SQL;
CREATE OR REPLACE FUNCTION get_rid(t TEXT) RETURNS BIGINT AS $$ SELECT id FROM recipes WHERE title=t LIMIT 1; $$ LANGUAGE SQL;

-- 1宫保鸡丁
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('宫保鸡丁','经典川菜，鸡肉嫩滑花生酥脆，麻辣鲜香。','https://placehold.co/400x300/ef4444/white?text=宫保鸡丁','https://www.bilibili.com/video/BV1GJ411x7jY',15,20,'中级','川菜',520,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('宫保鸡丁'),get_ing_id('鸡胸肉'),300,'克'),(get_rid('宫保鸡丁'),get_ing_id('花生米'),50,'克'),(get_rid('宫保鸡丁'),get_ing_id('干辣椒'),10,'个'),(get_rid('宫保鸡丁'),get_ing_id('花椒'),1,'克'),(get_rid('宫保鸡丁'),get_ing_id('葱'),2,'根'),(get_rid('宫保鸡丁'),get_ing_id('姜'),1,'块'),(get_rid('宫保鸡丁'),get_ing_id('蒜'),3,'瓣'),(get_rid('宫保鸡丁'),get_ing_id('生抽'),2,'勺'),(get_rid('宫保鸡丁'),get_ing_id('料酒'),1,'勺'),(get_rid('宫保鸡丁'),get_ing_id('淀粉'),1,'勺'),(get_rid('宫保鸡丁'),get_ing_id('白糖'),1,'勺'),(get_rid('宫保鸡丁'),get_ing_id('醋'),1,'勺');
INSERT INTO recipe_steps(recipe_id,step_number,instruction,timer_minutes) VALUES (get_rid('宫保鸡丁'),1,'鸡胸肉切丁加料酒盐淀粉腌制',15),(get_rid('宫保鸡丁'),2,'调碗汁：醋糖生抽老抽淀粉水搅匀',2),(get_rid('宫保鸡丁'),3,'锅中加油花生米小火炸至金黄',3),(get_rid('宫保鸡丁'),4,'锅留底油放干辣椒花椒炒香',1),(get_rid('宫保鸡丁'),5,'加鸡丁翻炒变色加葱姜蒜',3),(get_rid('宫保鸡丁'),6,'倒入碗汁翻炒加花生米拌匀',2);

-- 2番茄炒蛋
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('番茄炒蛋','国民家常菜，酸甜可口营养丰富。','https://placehold.co/400x300/f59e0b/white?text=番茄炒蛋','https://www.bilibili.com/video/BV1GJ411x7jY',5,10,'初级','家常菜',320,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('番茄炒蛋'),get_ing_id('番茄'),3,'个'),(get_rid('番茄炒蛋'),get_ing_id('鸡蛋'),3,'个'),(get_rid('番茄炒蛋'),get_ing_id('葱'),1,'根'),(get_rid('番茄炒蛋'),get_ing_id('白糖'),1,'勺'),(get_rid('番茄炒蛋'),get_ing_id('生抽'),1,'勺');

-- 3麻婆豆腐
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('麻婆豆腐','四川名菜麻辣鲜香豆腐嫩滑。','https://placehold.co/400x300/ef4444/white?text=麻婆豆腐',NULL,10,15,'中级','川菜',380,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('麻婆豆腐'),get_ing_id('豆腐'),2,'块'),(get_rid('麻婆豆腐'),get_ing_id('猪肉'),100,'克'),(get_rid('麻婆豆腐'),get_ing_id('豆瓣酱'),2,'勺'),(get_rid('麻婆豆腐'),get_ing_id('花椒'),1,'克'),(get_rid('麻婆豆腐'),get_ing_id('葱'),2,'根'),(get_rid('麻婆豆腐'),get_ing_id('姜'),1,'块'),(get_rid('麻婆豆腐'),get_ing_id('蒜'),3,'瓣'),(get_rid('麻婆豆腐'),get_ing_id('淀粉'),1,'勺');

-- 4清蒸鲈鱼
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('清蒸鲈鱼','粤式经典鱼肉鲜嫩原汁原味。','https://placehold.co/400x300/6366f1/white?text=清蒸鲈鱼','https://www.bilibili.com/video/BV1GJ411x7jY',10,12,'高级','粤菜',360,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('清蒸鲈鱼'),get_ing_id('鲈鱼'),1,'条'),(get_rid('清蒸鲈鱼'),get_ing_id('葱'),2,'根'),(get_rid('清蒸鲈鱼'),get_ing_id('姜'),1,'块'),(get_rid('清蒸鲈鱼'),get_ing_id('生抽'),2,'勺'),(get_rid('清蒸鲈鱼'),get_ing_id('料酒'),1,'勺');

-- 5红烧肉
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('红烧肉','经典家常菜五花肉软糯入味。','https://placehold.co/400x300/ef4444/white?text=红烧肉',NULL,15,60,'中级','湘菜',680,4,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('红烧肉'),get_ing_id('五花肉'),500,'克'),(get_rid('红烧肉'),get_ing_id('姜'),1,'块'),(get_rid('红烧肉'),get_ing_id('葱'),2,'根'),(get_rid('红烧肉'),get_ing_id('生抽'),2,'勺'),(get_rid('红烧肉'),get_ing_id('料酒'),2,'勺'),(get_rid('红烧肉'),get_ing_id('白糖'),2,'勺');

-- 6鱼香肉丝
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('鱼香肉丝','川菜代表酸甜微辣肉丝嫩滑。','https://placehold.co/400x300/f59e0b/white?text=鱼香肉丝',NULL,15,15,'中级','川菜',450,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('鱼香肉丝'),get_ing_id('猪肉'),200,'克'),(get_rid('鱼香肉丝'),get_ing_id('木耳'),10,'克'),(get_rid('鱼香肉丝'),get_ing_id('胡萝卜'),1,'根'),(get_rid('鱼香肉丝'),get_ing_id('青椒'),2,'个'),(get_rid('鱼香肉丝'),get_ing_id('葱'),2,'根'),(get_rid('鱼香肉丝'),get_ing_id('姜'),1,'块'),(get_rid('鱼香肉丝'),get_ing_id('蒜'),3,'瓣'),(get_rid('鱼香肉丝'),get_ing_id('豆瓣酱'),1,'勺'),(get_rid('鱼香肉丝'),get_ing_id('醋'),1,'勺'),(get_rid('鱼香肉丝'),get_ing_id('白糖'),1,'勺'),(get_rid('鱼香肉丝'),get_ing_id('淀粉'),1,'勺');

-- 7青椒肉丝
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('青椒肉丝','快手家常菜青椒脆嫩肉丝鲜美。','https://placehold.co/400x300/22c55e/white?text=青椒肉丝',NULL,10,10,'初级','家常菜',380,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('青椒肉丝'),get_ing_id('猪肉'),150,'克'),(get_rid('青椒肉丝'),get_ing_id('青椒'),3,'个'),(get_rid('青椒肉丝'),get_ing_id('生抽'),1,'勺'),(get_rid('青椒肉丝'),get_ing_id('料酒'),1,'勺'),(get_rid('青椒肉丝'),get_ing_id('淀粉'),1,'勺'),(get_rid('青椒肉丝'),get_ing_id('蒜'),2,'瓣');

-- 8糖醋排骨
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('糖醋排骨','酸甜可口外酥里嫩色泽红亮。','https://placehold.co/400x300/f97316/white?text=糖醋排骨','https://www.bilibili.com/video/BV1GJ411x7jY',15,40,'中级','浙菜',580,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('糖醋排骨'),get_ing_id('排骨'),500,'克'),(get_rid('糖醋排骨'),get_ing_id('料酒'),2,'勺'),(get_rid('糖醋排骨'),get_ing_id('生抽'),2,'勺'),(get_rid('糖醋排骨'),get_ing_id('醋'),2,'勺'),(get_rid('糖醋排骨'),get_ing_id('白糖'),3,'勺'),(get_rid('糖醋排骨'),get_ing_id('姜'),1,'块'),(get_rid('糖醋排骨'),get_ing_id('葱'),2,'根');

-- 9地三鲜
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('地三鲜','东北经典土豆软糯茄子入味青椒鲜香。','https://placehold.co/400x300/22c55e/white?text=地三鲜',NULL,10,20,'初级','家常菜',340,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('地三鲜'),get_ing_id('土豆'),2,'个'),(get_rid('地三鲜'),get_ing_id('茄子'),2,'个'),(get_rid('地三鲜'),get_ing_id('青椒'),2,'个'),(get_rid('地三鲜'),get_ing_id('蒜'),3,'瓣'),(get_rid('地三鲜'),get_ing_id('生抽'),2,'勺'),(get_rid('地三鲜'),get_ing_id('淀粉'),1,'勺');

-- 10土豆炖牛肉
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('土豆炖牛肉','牛肉软烂土豆绵密汤汁浓郁。','https://placehold.co/400x300/f97316/white?text=土豆炖牛肉',NULL,15,50,'高级','家常菜',620,4,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('土豆炖牛肉'),get_ing_id('牛肉'),500,'克'),(get_rid('土豆炖牛肉'),get_ing_id('土豆'),3,'个'),(get_rid('土豆炖牛肉'),get_ing_id('胡萝卜'),1,'根'),(get_rid('土豆炖牛肉'),get_ing_id('洋葱'),1,'个'),(get_rid('土豆炖牛肉'),get_ing_id('姜'),1,'块'),(get_rid('土豆炖牛肉'),get_ing_id('生抽'),2,'勺'),(get_rid('土豆炖牛肉'),get_ing_id('料酒'),2,'勺');

-- 11蒜蓉虾仁
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('蒜蓉虾仁','鲜嫩弹牙高蛋白低脂肪。','https://placehold.co/400x300/6366f1/white?text=蒜蓉虾仁','https://www.bilibili.com/video/BV1GJ411x7jY',10,8,'初级','粤菜',280,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('蒜蓉虾仁'),get_ing_id('虾仁'),300,'克'),(get_rid('蒜蓉虾仁'),get_ing_id('蒜'),5,'瓣'),(get_rid('蒜蓉虾仁'),get_ing_id('葱'),1,'根'),(get_rid('蒜蓉虾仁'),get_ing_id('料酒'),1,'勺'),(get_rid('蒜蓉虾仁'),get_ing_id('淀粉'),1,'勺');

-- 12酸辣土豆丝
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('酸辣土豆丝','酸辣爽脆百吃不厌。','https://placehold.co/400x300/f59e0b/white?text=酸辣土豆丝',NULL,10,8,'初级','家常菜',260,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('酸辣土豆丝'),get_ing_id('土豆'),2,'个'),(get_rid('酸辣土豆丝'),get_ing_id('干辣椒'),5,'个'),(get_rid('酸辣土豆丝'),get_ing_id('花椒'),1,'克'),(get_rid('酸辣土豆丝'),get_ing_id('醋'),1,'勺'),(get_rid('酸辣土豆丝'),get_ing_id('葱'),1,'根');

-- 13凉拌黄瓜
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('凉拌黄瓜','清爽可口蒜香浓郁夏日必备。','https://placehold.co/400x300/22c55e/white?text=凉拌黄瓜',NULL,10,0,'初级','凉菜',80,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('凉拌黄瓜'),get_ing_id('黄瓜'),2,'根'),(get_rid('凉拌黄瓜'),get_ing_id('蒜'),3,'瓣'),(get_rid('凉拌黄瓜'),get_ing_id('醋'),1,'勺'),(get_rid('凉拌黄瓜'),get_ing_id('生抽'),1,'勺'),(get_rid('凉拌黄瓜'),get_ing_id('白糖'),1,'勺');

-- 14葱油拌面
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('葱油拌面','上海特色葱香浓郁简单快手。','https://placehold.co/400x300/6366f1/white?text=葱油拌面',NULL,5,10,'初级','主食',420,1,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('葱油拌面'),get_ing_id('面条'),200,'克'),(get_rid('葱油拌面'),get_ing_id('葱'),3,'根'),(get_rid('葱油拌面'),get_ing_id('生抽'),2,'勺'),(get_rid('葱油拌面'),get_ing_id('白糖'),1,'勺');

-- 15家常豆腐
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('家常豆腐','豆腐外焦里嫩搭配青椒木耳。','https://placehold.co/400x300/22c55e/white?text=家常豆腐',NULL,10,12,'初级','家常菜',310,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('家常豆腐'),get_ing_id('豆腐'),2,'块'),(get_rid('家常豆腐'),get_ing_id('青椒'),2,'个'),(get_rid('家常豆腐'),get_ing_id('木耳'),10,'克'),(get_rid('家常豆腐'),get_ing_id('葱'),1,'根'),(get_rid('家常豆腐'),get_ing_id('生抽'),1,'勺'),(get_rid('家常豆腐'),get_ing_id('淀粉'),1,'勺');

-- 16回锅肉
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('回锅肉','川菜之首香辣可口下饭神器。','https://placehold.co/400x300/ef4444/white?text=回锅肉',NULL,15,20,'中级','川菜',560,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('回锅肉'),get_ing_id('五花肉'),300,'克'),(get_rid('回锅肉'),get_ing_id('青椒'),2,'个'),(get_rid('回锅肉'),get_ing_id('蒜'),3,'瓣'),(get_rid('回锅肉'),get_ing_id('豆瓣酱'),2,'勺'),(get_rid('回锅肉'),get_ing_id('生抽'),1,'勺');

-- 17可乐鸡翅
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('可乐鸡翅','甜香可口做法简单新手也能做。','https://placehold.co/400x300/ef4444/white?text=可乐鸡翅',NULL,10,25,'初级','家常菜',420,3,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('可乐鸡翅'),get_ing_id('鸡胸肉'),500,'克'),(get_rid('可乐鸡翅'),get_ing_id('生抽'),2,'勺'),(get_rid('可乐鸡翅'),get_ing_id('姜'),1,'块'),(get_rid('可乐鸡翅'),get_ing_id('料酒'),1,'勺');

-- 18蛋炒饭
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('蛋炒饭','粒粒分明蛋香四溢。','https://placehold.co/400x300/f59e0b/white?text=蛋炒饭',NULL,5,8,'初级','主食',450,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('蛋炒饭'),get_ing_id('大米'),300,'克'),(get_rid('蛋炒饭'),get_ing_id('鸡蛋'),2,'个'),(get_rid('蛋炒饭'),get_ing_id('葱'),1,'根'),(get_rid('蛋炒饭'),get_ing_id('胡萝卜'),1,'根');

-- 19香菇青菜
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('香菇青菜','清淡健康香菇鲜美青菜爽口。','https://placehold.co/400x300/22c55e/white?text=香菇青菜',NULL,5,8,'初级','家常菜',120,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('香菇青菜'),get_ing_id('香菇'),8,'个'),(get_rid('香菇青菜'),get_ing_id('白菜'),1,'颗'),(get_rid('香菇青菜'),get_ing_id('蒜'),2,'瓣'),(get_rid('香菇青菜'),get_ing_id('生抽'),1,'勺');

-- 20西红柿鸡蛋汤
INSERT INTO recipes(title,description,image_url,video_url,prep_time,cook_time,difficulty,cuisine_type,calories,servings,status) VALUES
('西红柿鸡蛋汤','酸甜开胃制作简单。','https://placehold.co/400x300/f59e0b/white?text=西红柿鸡蛋汤',NULL,5,10,'初级','汤羹',160,2,'published');
INSERT INTO recipe_ingredients(recipe_id,ingredient_id,quantity,unit) VALUES (get_rid('西红柿鸡蛋汤'),get_ing_id('番茄'),2,'个'),(get_rid('西红柿鸡蛋汤'),get_ing_id('鸡蛋'),2,'个'),(get_rid('西红柿鸡蛋汤'),get_ing_id('葱'),1,'根'),(get_rid('西红柿鸡蛋汤'),get_ing_id('淀粉'),1,'勺');

DROP FUNCTION IF EXISTS get_ing_id;
DROP FUNCTION IF EXISTS get_rid;
