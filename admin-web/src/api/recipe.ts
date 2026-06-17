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

/** 获取推荐菜品列表 */
export function getRecommendedRecipes() {
    return http.get<ApiResult<Recipe[]>>('/recommend/recipes')
}

/** 获取菜品详情 */
export function getRecipeDetail(id: number) {
    return http.get<ApiResult<Recipe>>(`/recipe/${id}`)
}

/** 搜索菜品 */
export function searchRecipes(query: string) {
    return http.get<ApiResult<Recipe[]>>('/recipe/search', { params: { q: query } })
}

/** AI 生成菜谱 */
export function aiGenerateRecipe(dishName: string, ingredients?: string) {
    return http.post<ApiResult<any>>('/recipe/ai/generate', { dishName, availableIngredients: ingredients })
}
