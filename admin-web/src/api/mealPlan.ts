import http from './request'
import type { ApiResult } from './request'

export interface MealPlan {
    id: number
    name: string
    startDate: string
    endDate: string
    planType: string
    status: string
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

/** 根据就餐记录调整用餐计划 */
export function adjustMealPlan(preferences?: string, allergens?: string, healthGoal?: string, mealHistory?: string) {
    return http.post<ApiResult<any>>('/meal-plan/adjust', mealHistory, {
        params: { preferences, allergens, healthGoal }
    })
}

/** 营养分析 */
export function analyzeNutrition(mealPlanJson: string) {
    return http.post<ApiResult<any>>('/meal-plan/nutrition-analysis', mealPlanJson)
}

/** 食材替换建议 */
export function suggestSubstitution(dishName: string, ingredient: string, reason?: string) {
    return http.post<ApiResult<any>>('/meal-plan/substitution', null, {
        params: { dishName, ingredient, reason }
    })
}

/** 生成采购清单 */
export function getShoppingList(userId?: number, planType?: string) {
    return http.get<ApiResult<any[]>>('/meal-plan/shopping-list', {
        params: { userId, planType }
    })
}

/** 获取本周用餐计划 */
export function getWeeklyPlan() {
    return http.get<ApiResult<MealPlan>>('/meal-plan/weekly')
}
