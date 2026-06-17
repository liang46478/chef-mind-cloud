#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re, os

CUISINE_COLORS = {
    '川菜': 'ef4444', '粤菜': '6366f1', '湘菜': 'f97316', '鲁菜': '3b82f6',
    '苏菜': '8b5cf6', '浙菜': 'ec4899', '闽菜': '14b8a6', '徽菜': '84cc16',
    '东北菜': '22c55e', '西北菜': 'eab308', '湖北菜': '06b6d4', '云南菜': 'f43f5e',
    '家常菜': '10b981', '汤羹': '0ea5e9', '凉菜': 'a855f7', '主食': 'f59e0b',
}

def guess_cuisine(name):
    kw_map = {'川菜':['水煮','辣子','麻婆','宫保','鱼香','回锅','夫妻','毛血旺','酸菜鱼','干煸','口水鸡','蚂蚁上树','灯影','担担','宜宾','麻辣香锅','川北'],
              '粤菜':['白切','叉烧','煲仔','干炒','虾饺','肠粉','豉汁','蚝油','老火','蜜汁','咕咾','杨枝'],
              '湘菜':['剁椒','小炒','外婆菜','腊味','口味虾','擂辣椒','土匪','酸豆角','湘味','干锅'],
              '鲁菜':['糖醋鲤鱼','九转','葱烧','木须肉','扒鸡','四喜','油焖','醋溜','拔丝'],
              '苏菜':['松鼠','清炖','大煮','盐水鸭','蟹粉','无锡'],
              '浙菜':['西湖','龙井','荷叶','宋嫂','叫花','宁波'],
              '闽菜':['佛跳墙','荔枝肉','沙茶','海蛎','鱼丸'],
              '徽菜':['臭鳜','毛豆腐','一品锅','问政'],
              '东北菜':['锅包肉','地三鲜','乱炖','粉条','酱骨架','小鸡炖','酸菜炖','大拉皮','韭菜盒子','饭包','猪肉炖'],
              '西北菜':['大盘鸡','兰州','手抓','肉夹馍','凉皮','泡馍','烤羊肉','臊子'],
              '湖北菜':['热干面','武昌','莲藕','三鲜豆皮','珍珠丸子'],
              '云南菜':['过桥','汽锅','小锅米线','傣味','玫瑰饼'],
              '主食':['炒饭','炒面','拌面','面条','米线','饺子','包子','饼','馒头','饭','面'],
              '汤羹':['汤','羹'],
              '凉菜':['凉拌','皮蛋豆腐','蒜泥白肉','红油','芥末墩','皮蛋','口水鸡']}
    for cuisine, kws in kw_map.items():
        for kw in kws:
            if kw in name: return cuisine
    return '家常菜'

sql_dir = '../src/main/resources/db/migration/'
all_names = []

for fn in ['V4__seed_recipe_data.sql', 'V5__massive_seed_data.sql']:
    path = os.path.join(sql_dir, fn)
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    for line in lines:
        s = line.strip()
        # Match lines starting with '(' that have recipe data
        # Format: ('菜名','描述','图片url',...
        if s.startswith("('"):
            parts = s.split("','")
            if len(parts) >= 2:
                name = parts[0][2:]  # Remove ('
                if name and not any(kw in name for kw in ['菜名','食材','INSERT','VALUES','--']):
                    if name and name not in all_names:
                        all_names.append(name)

print(f"Found {len(all_names)} recipe names")

# 生成SQL
sql_lines = ["-- V6: 批量更新菜谱图片（按菜系配色）", "BEGIN;"]
count = 0
for name in all_names:
    cuisine = guess_cuisine(name)
    color = CUISINE_COLORS.get(cuisine, '10b981')
    url = f"https://placehold.co/600x400/{color}/white?text={name}"
    sql_lines.append(f"UPDATE recipes SET image_url = '{url}' WHERE title = '{name}';")
    count += 1

sql_lines.append("COMMIT;")

out_path = os.path.join(sql_dir, 'V6__update_recipe_images.sql')
with open(out_path, 'w', encoding='utf-8') as f:
    f.write('\n'.join(sql_lines))

from collections import Counter
c = Counter(guess_cuisine(n) for n in all_names)
for cuisine, cnt in c.most_common():
    print(f"  {cuisine}: {cnt}道")
print(f"Total: {count} recipes updated")
