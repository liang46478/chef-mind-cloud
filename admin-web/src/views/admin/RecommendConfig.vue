<script setup lang="ts">
import { ref } from 'vue'
import Card from 'primevue/card'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import ToggleSwitch from 'primevue/toggleswitch'

const config = ref({
    enableCollaborativeFiltering: true,
    enableContentBased: true,
    enableHotRank: true,
    cfWeight: 0.4,
    cbWeight: 0.3,
    hotWeight: 0.3,
    coldStartStrategy: 'popular',
    recommendLimit: 20,
})

function saveConfig() {
    // TODO: 调用 API 保存推荐策略
    console.log('Save config:', config.value)
}
</script>

<template>
    <div>
        <h1 class="text-2xl font-bold text-gray-800 mb-6">🎯 推荐策略配置</h1>

        <Card>
            <template #content>
                <div class="flex flex-col gap-6">
                    <!-- 算法开关 -->
                    <div class="space-y-4">
                        <h3 class="font-semibold text-gray-700">推荐算法</h3>
                        <div class="flex items-center justify-between">
                            <span>协同过滤 (Collaborative Filtering)</span>
                            <ToggleSwitch v-model="config.enableCollaborativeFiltering" />
                        </div>
                        <div class="flex items-center justify-between">
                            <span>基于内容推荐 (Content-Based)</span>
                            <ToggleSwitch v-model="config.enableContentBased" />
                        </div>
                        <div class="flex items-center justify-between">
                            <span>热门排行 (Hot Rank)</span>
                            <ToggleSwitch v-model="config.enableHotRank" />
                        </div>
                    </div>

                    <!-- 权重配置 -->
                    <div class="space-y-4">
                        <h3 class="font-semibold text-gray-700">算法权重</h3>
                        <div class="grid grid-cols-3 gap-4">
                            <div>
                                <label class="block text-sm mb-1">协同过滤权重</label>
                                <InputNumber v-model="config.cfWeight" :min="0" :max="1" :step="0.1" />
                            </div>
                            <div>
                                <label class="block text-sm mb-1">内容推荐权重</label>
                                <InputNumber v-model="config.cbWeight" :min="0" :max="1" :step="0.1" />
                            </div>
                            <div>
                                <label class="block text-sm mb-1">热门权重</label>
                                <InputNumber v-model="config.hotWeight" :min="0" :max="1" :step="0.1" />
                            </div>
                        </div>
                    </div>

                    <!-- 其他配置 -->
                    <div class="space-y-4">
                        <h3 class="font-semibold text-gray-700">其他设置</h3>
                        <div>
                            <label class="block text-sm mb-1">冷启动策略</label>
                            <div class="text-sm text-gray-500">使用热门推荐为新用户服务</div>
                        </div>
                        <div>
                            <label class="block text-sm mb-1">推荐数量上限</label>
                            <InputNumber v-model="config.recommendLimit" :min="5" :max="50" />
                        </div>
                    </div>

                    <Button label="保存配置" severity="success" @click="saveConfig" class="w-40" />
                </div>
            </template>
        </Card>
    </div>
</template>
