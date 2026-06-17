import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/Login.vue'),
    },
    {
        path: '/',
        name: 'home',
        component: () => import('@/views/user/Home.vue'),
    },
    {
        path: '/recipe/:id',
        name: 'recipe-detail',
        component: () => import('@/views/user/RecipeDetail.vue'),
    },
    {
        path: '/search',
        name: 'search',
        component: () => import('@/views/user/SearchResult.vue'),
    },
    {
        path: '/meal-plan',
        name: 'meal-plan',
        component: () => import('@/views/user/MealPlan.vue'),
    },
    {
        path: '/meal-records',
        name: 'meal-records',
        component: () => import('@/views/user/MealRecords.vue'),
    },
    {
        path: '/profile',
        name: 'profile',
        component: () => import('@/views/user/Profile.vue'),
    },
    // 管理后台路由
    {
        path: '/admin',
        name: 'admin',
        component: () => import('@/views/admin/Layout.vue'),
        children: [
            {
                path: '',
                redirect: '/admin/dashboard',
            },
            {
                path: 'dashboard',
                name: 'admin-dashboard',
                component: () => import('@/views/admin/Dashboard.vue'),
            },
            {
                path: 'users',
                name: 'admin-users',
                component: () => import('@/views/admin/Users.vue'),
            },
            {
                path: 'recipes',
                name: 'admin-recipes',
                component: () => import('@/views/admin/Recipes.vue'),
            },
            {
                path: 'ingredients',
                name: 'admin-ingredients',
                component: () => import('@/views/admin/Ingredients.vue'),
            },
            {
                path: 'prompts',
                name: 'admin-prompts',
                component: () => import('@/views/admin/Prompts.vue'),
            },
            {
                path: 'recommend-config',
                name: 'admin-recommend-config',
                component: () => import('@/views/admin/RecommendConfig.vue'),
            },
        ],
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router
