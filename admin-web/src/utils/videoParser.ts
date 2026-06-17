/**
 * 视频链接解析工具
 * 自动识别 B站/抖音/YouTube/快手 等平台视频链接，返回可嵌入的 URL
 */

export interface VideoInfo {
    /** 可嵌入的 iframe URL */
    embedUrl: string
    /** 视频封面图 */
    coverUrl?: string
    /** 平台名称 */
    platform: string
    /** 原始链接 */
    originalUrl: string
    /** 是否可嵌入 */
    embeddable: boolean
}

/**
 * 解析视频链接，返回嵌入信息
 */
export function parseVideoUrl(url: string): VideoInfo | null {
    if (!url || url.trim() === '') return null

    const trimmed = url.trim()

    // Bilibili 视频
    const bilibiliMatch = trimmed.match(
        /(?:https?:\/\/)?(?:www\.)?bilibili\.com\/video\/(BV[a-zA-Z0-9]+)/
    )
    if (bilibiliMatch) {
        return {
            embedUrl: `https://player.bilibili.com/player.html?bvid=${bilibiliMatch[1]}&autoplay=0`,
            platform: 'bilibili',
            originalUrl: trimmed,
            embeddable: true,
        }
    }

    // Bilibili 短链接 (bv)
    const bilibiliShortMatch = trimmed.match(/^(BV[a-zA-Z0-9]{10})/)
    if (bilibiliShortMatch) {
        return {
            embedUrl: `https://player.bilibili.com/player.html?bvid=${bilibiliShortMatch[1]}&autoplay=0`,
            platform: 'bilibili',
            originalUrl: trimmed,
            embeddable: true,
        }
    }

    // YouTube 视频
    const youtubeMatch = trimmed.match(
        /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([a-zA-Z0-9_-]{11})/
    )
    if (youtubeMatch) {
        return {
            embedUrl: `https://www.youtube.com/embed/${youtubeMatch[1]}`,
            platform: 'youtube',
            originalUrl: trimmed,
            embeddable: true,
        }
    }

    // 抖音视频 (douyin.com)
    const douyinMatch = trimmed.match(
        /(?:https?:\/\/)?(?:www\.)?douyin\.com\/video\/(\d+)/
    )
    if (douyinMatch) {
        return {
            embedUrl: trimmed, // 抖音使用原始链接跳转
            coverUrl: `https://www.douyin.com/video/${douyinMatch[1]}`,
            platform: 'douyin',
            originalUrl: trimmed,
            embeddable: false,
        }
    }

    // 腾讯视频
    const tencentMatch = trimmed.match(
        /(?:https?:\/\/)?v\.qq\.com\/.*\/([a-zA-Z0-9]+)\.html/
    )
    if (tencentMatch) {
        return {
            embedUrl: `https://v.qq.com/txp/iframe/player.html?vid=${tencentMatch[1]}&autoplay=0`,
            platform: 'tencent',
            originalUrl: trimmed,
            embeddable: true,
        }
    }

    // 通用 iframe 嵌入（直接视频 URL 或已嵌入链接）
    if (trimmed.includes('player.') || trimmed.includes('embed')) {
        return {
            embedUrl: trimmed,
            platform: '通用',
            originalUrl: trimmed,
            embeddable: true,
        }
    }

    // 无法识别的链接，直接作为原始链接展示
    return {
        embedUrl: trimmed,
        platform: '其他',
        originalUrl: trimmed,
        embeddable: false,
    }
}

/**
 * 获取平台图标
 */
export function getPlatformIcon(platform: string): string {
    const icons: Record<string, string> = {
        bilibili: 'pi pi-play',
        youtube: 'pi pi-play',
        douyin: 'pi pi-music',
        tencent: 'pi pi-video',
    }
    return icons[platform] || 'pi pi-link'
}

/**
 * 获取平台颜色
 */
export function getPlatformColor(platform: string): string {
    const colors: Record<string, string> = {
        bilibili: '#fb7299',
        youtube: '#ff0000',
        douyin: '#000000',
        tencent: '#0052d9',
    }
    return colors[platform] || '#6366f1'
}
