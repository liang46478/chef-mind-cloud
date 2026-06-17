import http from './request'
import type { ApiResult } from './request'

export interface UserInfo {
    id: number
    username: string
    nickname: string
    avatar: string
    email: string
    phone: string
    cuisinePreference: string
    allergens: string
    healthGoal: string
}

export interface UserPreference {
    cuisineType: string
    taste: string
    spicinessLevel: string
    healthGoal: string
    allergens: string[]
}

export interface MealRecord {
    id: number
    userId?: number
    recipeId: number
    recipeName: string
    mealType: string
    rating: number
    feedback: string
    ateAt: string
}

/** 用户登录 */
export function login(username: string, password: string) {
    return http.post<ApiResult<{ token: string }>>('/user/auth/login', { username, password })
}

/** 获取用户信息（需登录） */
export function getUserProfile() {
    return http.get<ApiResult<UserInfo>>('/user/auth/profile')
}

/** 更新用户资料 */
export function updatePreference(pref: UserPreference) {
    return http.put<ApiResult<null>>('/user/auth/profile', pref)
}

/** 获取就餐记录 */
export function getMealRecords(userId: number) {
    return http.get<ApiResult<MealRecord[]>>('/user/meal-records', { params: { userId } })
}

/** 添加就餐记录 */
export function addMealRecord(record: Partial<MealRecord>) {
    return http.post<ApiResult<MealRecord>>('/user/meal-records', record)
}
