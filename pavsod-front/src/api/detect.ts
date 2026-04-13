// 视频检测相关 API 封装

const BASE_URL = 'http://localhost:2778'

// 上传视频接口
export interface UploadVideoParams {
  userId: string
  video: File
  frame: number
  video_type: '2d' | 'panoramic'
  duration: number
}

// 上传成功后返回视频的 ID
export type UploadVideoResult = string

// AI 评价结果
export interface AIEvaluationResult {
  videoAvatar: string
  aiSuggestion: string
}

// 检测结果（视频 URL）
export interface DetectionResult {
  originalUrl: string
  saliencyUrl: string
}

export async function uploadVideo(params: UploadVideoParams): Promise<UploadVideoResult> {
  const token = localStorage.getItem('pavsod_token')

  // 使用 FormData 上传文件
  const formData = new FormData()
  formData.append('userId', params.userId)
  formData.append('file', params.video)  // 后端接收字段名为 file
  formData.append('frame', params.frame.toString())
  formData.append('video_type', params.video_type === 'panoramic' ? '3d' : '2d')
  formData.append('duration', params.duration.toString())

  console.log('[API Upload] 开始上传:', params.video.name)
  console.log('[API Upload] Token:', token ? '存在' : '不存在')

  const response = await fetch(`${BASE_URL}/detect/upload`, {
    method: 'POST',
    headers: {
      ...(token && { 'token': token }),
      // 注意：FormData 不需要设置 Content-Type，浏览器会自动设置并添加 boundary
    },
    body: formData,
  })

  const data = await response.json()

  if (!response.ok || data.code !== 200) {
    const errorMsg = data.message || data.msg || '上传失败'
    throw new Error(errorMsg)
  }

  return data.data as UploadVideoResult
}

// 获取 AI 评价
export async function getAIEvaluation(userId: string, videoId: string): Promise<AIEvaluationResult> {
  const token = localStorage.getItem('pavsod_token')

  const response = await fetch(`${BASE_URL}/detect/ai?userId=${encodeURIComponent(userId)}&video_avatar=${encodeURIComponent(videoId)}`, {
    method: 'GET',
    headers: {
      ...(token && { 'token': token }),
    },
  })

  const data = await response.json()

  if (!response.ok || data.code !== 200) {
    const errorMsg = data.message || data.msg || '获取评价失败'
    throw new Error(errorMsg)
  }

  return {
    videoAvatar: data.data.video_avatar,
    aiSuggestion: data.data.ai_suggestion,
  }
}

// 获取检测结果视频 URL
export async function getDetectionResult(userId: string, videoId: string): Promise<DetectionResult> {
  const token = localStorage.getItem('pavsod_token')

  const response = await fetch(`${BASE_URL}/detect/result?userId=${encodeURIComponent(userId)}&videoId=${encodeURIComponent(videoId)}`, {
    method: 'GET',
    headers: {
      ...(token && { 'token': token }),
    },
  })

  const data = await response.json()

  if (!response.ok || data.code !== 200) {
    const errorMsg = data.message || data.msg || '获取检测结果失败'
    throw new Error(errorMsg)
  }

  return {
    originalUrl: data.data.originalUrl || data.data.video_avatar,
    saliencyUrl: data.data.saliencyUrl || data.data.video_url,
  }
}
