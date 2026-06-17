<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '@/api/admin'
import Card from 'primevue/card'
import * as echarts from 'echarts'

const stats = ref([
    { label: '总用户数', value: '-', icon: 'pi pi-users', color: 'bg-blue-500' },
    { label: '菜品总数', value: '-', icon: 'pi pi-book', color: 'bg-emerald-500' },
    { label: '今日推荐', value: '-', icon: 'pi pi-thumbs-up', color: 'bg-amber-500' },
    { label: '本周计划', value: '-', icon: 'pi pi-calendar', color: 'bg-purple-500' },
])

onMounted(async () => {
    try {
        const res = await getDashboardStats()
        if (res.data.data) {
            const d = res.data.data
            stats.value = [
                { label: '总用户数', value: String(d.totalUsers || 0), icon: 'pi pi-users', color: 'bg-blue-500' },
                { label: '菜品总数', value: String(d.totalRecipes || 0), icon: 'pi pi-book', color: 'bg-emerald-500' },
                { label: '今日推荐', value: String(d.dailyRecommendations || 0), icon: 'pi pi-thumbs-up', color: 'bg-amber-500' },
                { label: '本周计划', value: String(d.weeklyPlans || 0), icon: 'pi pi-calendar', color: 'bg-purple-500' },
            ]
        }
    } catch { /* use defaults */ }

    initCharts()
})

function initCharts() {
    const userChart = echarts.init(document.getElementById('userChart')!)
    userChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], axisLabel: { color: '#666' } },
        yAxis: { type: 'value', axisLabel: { color: '#666' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        series: [{
            data: [12, 18, 15, 22, 28, 35, 30],
            type: 'line', smooth: true,
            lineStyle: { color: '#10b981', width: 3 },
            areaStyle: { color: 'rgba(16, 185, 129, 0.1)' },
            symbol: 'circle', symbolSize: 8,
        }]
    })

    const recipeChart = echarts.init(document.getElementById('recipeChart')!)
    recipeChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
            type: 'pie', radius: ['40%', '70%'],
            data: [
                { value: 85, name: '川菜', itemStyle: { color: '#ef4444' } },
                { value: 62, name: '粤菜', itemStyle: { color: '#10b981' } },
                { value: 48, name: '湘菜', itemStyle: { color: '#f59e0b' } },
                { value: 35, name: '鲁菜', itemStyle: { color: '#3b82f6' } },
                { value: 28, name: '家常菜', itemStyle: { color: '#8b5cf6' } },
            ],
            label: { show: true, formatter: '{b}: {c}' },
            emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
        }]
    })
}
</script>

<template>
    <div>
        <h1 class="text-2xl font-bold text-gray-800 mb-6">📊 数据看板</h1>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
            <Card v-for="stat in stats" :key="stat.label">
                <template #content>
                    <div class="flex items-center gap-4">
                        <div :class="stat.color" class="w-12 h-12 rounded-lg flex items-center justify-center">
                            <i :class="stat.icon" class="text-white text-xl" />
                        </div>
                        <div>
                            <p class="text-sm text-gray-500">{{ stat.label }}</p>
                            <p class="text-2xl font-bold">{{ stat.value }}</p>
                        </div>
                    </div>
                </template>
            </Card>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
                <template #title>📈 用户增长趋势</template>
                <template #content>
                    <div id="userChart" class="h-64 w-full"></div>
                </template>
            </Card>
            <Card>
                <template #title>🥘 菜品热度排行</template>
                <template #content>
                    <div id="recipeChart" class="h-64 w-full"></div>
                </template>
            </Card>
        </div>
    </div>
</template>
