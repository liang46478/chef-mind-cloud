import http from './request'
import type { ApiResult } from './request'

export interface Recipe {
    id: number
    title: string
    description: string
    imageUrl: string
    videoUrl?: string
    prepTime: number
    cookTime: number
    difficulty: string
    cuisineType: string
    calories: number
    servings: number
    ingredients: Ingredient[]
    steps: RecipeStep[]
    tags: string[]
    status: string
}

export interface Ingredient {
    id: number
    name: string
    quantity: number
    unit: string
    calories: number
}

export interface RecipeStep {
    stepNumber: number
    instruction: string
    imageUrl?: string
    videoUrl?: string
    timerMinutes: number
}

export function getRecommendedRecipes() {
    return http.get<ApiResult<Recipe[]>>('/recommend/recipes')
}

export function getRecipeDetail(id: number) {
    return http.get<ApiResult<Recipe>>(`/recipe/${id}`)
}

export function searchRecipes(query: string) {
    return http.get<ApiResult<Recipe[]>>('/recipe/search', { params: { q: query } })
}

export function aiGenerateRecipe(dishName: string, ingredients?: string) {
    return http.post<ApiResult<any>>('/recipe/ai/generate', { dishName, availableIngredients: ingredients })
}

/** 获取所有食材列表 */
export function getAllIngredients() {
    return http.get<ApiResult<any[]>>('/ingredients/list')
}

/** 按食材搜索菜谱 */
export function searchRecipesByIngredients(ids: number[], matchMode?: string) {
    return http.get<ApiResult<any[]>>('/recipe/by-ingredients', {
        params: { ids: ids.join(','), matchMode: matchMode || 'any' }
    })
}

/** AI 根据食材生成菜谱 */
export function aiGenerateByIngredients(ingredients: string[]) {
    return http.post<ApiResult<any>>('/recipe/ai/by-ingredients', { ingredients })
}
