import http from './request'
import type { ApiResult } from './request'

export interface DashboardStats {
    totalUsers: number
    totalRecipes: number
    dailyRecommendations: number
    weeklyPlans: number
}

/** 获取数据看板统计 */
export function getDashboardStats() {
    return http.get<ApiResult<DashboardStats>>('/admin/dashboard')
}

/** 获取用户列表 */
export function getUsers(params?: { page?: number; pageSize?: number }) {
    return http.get<ApiResult<{ records: any[]; total: number }>>('/admin/users', { params })
}

/** 禁用/启用用户 */
export function toggleUserStatus(userId: number) {
    return http.put<ApiResult<null>>(`/admin/users/${userId}/toggle-status`)
}

/** 获取菜品列表 */
export function getRecipes(params?: { page?: number; pageSize?: number; status?: string }) {
    return http.get<ApiResult<{ records: any[]; total: number }>>('/admin/recipes', { params })
}

/** 更新菜品状态 */
export function updateRecipeStatus(recipeId: number, status: string) {
    return http.put<ApiResult<null>>(`/admin/recipes/${recipeId}/status`, { status })
}

/** 保存推荐配置 */
export function saveRecommendConfig(config: any) {
    return http.post<ApiResult<null>>('/admin/recommend-config', config)
}

/** 获取推荐配置 */
export function getRecommendConfig() {
    return http.get<ApiResult<any>>('/admin/recommend-config')
}

/** 保存提示词 */
export function savePrompt(promptType: string, content: string) {
    return http.post<ApiResult<null>>('/admin/prompts', { type: promptType, content })
}

/** 获取提示词 */
export function getPrompt(promptType: string) {
    return http.get<ApiResult<string>>('/admin/prompts', { params: { type: promptType } })
}

/** 获取食材列表 */
export function getIngredients(params?: { page?: number; pageSize?: number; keyword?: string }) {
    return http.get<ApiResult<{ records: any[]; total: number }>>('/admin/ingredients', { params })
}
