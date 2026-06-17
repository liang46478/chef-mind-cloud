<script setup lang="ts">
import { computed } from 'vue'
import { parseVideoUrl, getPlatformIcon, getPlatformColor } from '@/utils/videoParser'

const props = defineProps<{
    videoUrl: string
    title?: string
}>()

const videoInfo = computed(() => parseVideoUrl(props.videoUrl))

const platformIcon = computed(() => videoInfo.value ? getPlatformIcon(videoInfo.value.platform) : 'pi pi-play')
const platformColor = computed(() => videoInfo.value ? getPlatformColor(videoInfo.value.platform) : '#6366f1')
</script>

<template>
    <div v-if="videoInfo" class="video-container bg-gray-900 rounded-xl overflow-hidden">
        <!-- 可嵌入的视频 -->
        <div v-if="videoInfo.embeddable" class="aspect-video relative">
            <iframe
                :src="videoInfo.embedUrl"
                :title="title || '做菜视频'"
                class="absolute inset-0 w-full h-full"
                allowfullscreen
                allow="autoplay; fullscreen"
                loading="lazy"
            />
        </div>

        <!-- 不可嵌入的平台（抖音等），显示外链卡片 -->
        <div v-else class="p-6 text-center">
            <a :href="videoInfo.originalUrl"
               target="_blank"
               rel="noopener noreferrer"
               class="inline-flex items-center gap-3 px-6 py-3 rounded-lg text-white font-medium transition-transform hover:scale-105"
               :style="{ backgroundColor: platformColor }">
                <i :class="platformIcon" />
                <span>在 {{ videoInfo.platform }} 观看视频</span>
                <i class="pi pi-external-link" />
            </a>
        </div>
    </div>
</template>
