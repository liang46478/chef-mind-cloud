import http from './request'
import type { ApiResult } from './request'

export interface MealPlan {
    id: number
    name: string
    startDate: string
    endDate: string
    planType: string
    items: MealPlanItem[]
}

export interface MealPlanItem {
    id: number
    date: string
    mealType: string
    recipeId: number
    recipeName: string
    notes: string
    status: string
}

/** 获取用餐计划 */
export function getMealPlan(planType: string) {
    return http.get<ApiResult<MealPlan>>('/meal-plan/current', { params: { type: planType } })
}

/** AI 生成用餐计划 */
export function generateMealPlan() {
    return http.post<ApiResult<MealPlan>>('/meal-plan/generate')
}

/** 获取本周用餐计划 */
export function getWeeklyPlan() {
    return http.get<ApiResult<MealPlan>>('/meal-plan/weekly')
}
