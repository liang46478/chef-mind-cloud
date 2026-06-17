<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Card from 'primevue/card'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import ToggleSwitch from 'primevue/toggleswitch'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import axios from 'axios'

const configs = ref<any[]>([])
const loading = ref(true)
const saving = ref(false)
const successMsg = ref('')

onMounted(async () => {
    try {
        const r = await axios.get('/api/admin/system/configs')
        configs.value = r.data.data || []
    } catch { configs.value = [] }
    finally { loading.value = false }
})

async function save(key: string, value: string) {
    saving.value = true
    try {
        await axios.put('/api/admin/system/configs', { config_key: key, config_value: value })
        successMsg.value = '保存成功'
        setTimeout(() => successMsg.value = '', 2000)
    } catch { successMsg.value = '保存失败' }
    finally { saving.value = false }
}

function getConfig(key: string): any {
    return configs.value.find(c => c.config_key === key)
}
</script>

<template>
    <div>
        <h1 class="text-2xl font-bold text-gray-800 mb-6">⚙️ 系统配置</h1>
        <Message v-if="successMsg" severity="success" class="mb-4">{{ successMsg }}</Message>

        <div v-if="loading" class="flex justify-center py-12"><ProgressSpinner /></div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card v-for="cfg in configs" :key="cfg.id">
                <template #title>{{ cfg.description || cfg.config_key }}</template>
                <template #content>
                    <div v-if="cfg.config_key === 'enable_registration' || cfg.config_key === 'require_email_verify'" class="flex items-center justify-between">
                        <span>启用</span>
                        <ToggleSwitch :modelValue="cfg.config_value === 'true'" @update:modelValue="v => { cfg.config_value = String(v); save(cfg.config_key, cfg.config_value) }" />
                    </div>
                    <div v-else class="flex gap-2">
                        <InputText v-model="cfg.config_value" class="flex-1" />
                        <Button label="保存" size="small" @click="save(cfg.config_key, cfg.config_value)" :loading="saving" />
                    </div>
                </template>
            </Card>
        </div>
    </div>
</template>
