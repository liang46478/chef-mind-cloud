package com.chefmind.mealplan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chefmind.mealplan.entity.MealPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MealPlanMapper extends BaseMapper<MealPlan> {
}
