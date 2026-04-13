from flask import Flask, request
import global_options as opts
import pymysql
import os
import cv2
import torch
import torchaudio
import numpy as np
from PIL import Image
import torchvision.transforms as transforms
import requests
import tempfile
import json
import shutil
import redis
from llm_suggest import llm_suggest
from aliyun import aliyunOssUpload


# CAVNet import
import sys
sys.path.insert(0, os.path.join(os.getcwd(), 'src'))
from models.CAVNet import cavnet

# Global model cache
model = None

def get_model():
    """Load CAVNet model (singleton pattern)"""
    global model
    if model is None:
        model_path = os.path.join('src', 'CAV-Net_models', 'final', 'CAVNet_final.pth')
        if not os.path.exists(model_path):
            raise FileNotFoundError(f"Model not found at {model_path}")

        # Create model in eval mode first to skip auto-loading pretrain weights
        # (since we're loading the full trained model anyway)
        model = cavnet()
        model.eval()  # Set to eval mode before loading state dict
        model.load_state_dict(torch.load(model_path, map_location='cuda'))
        model.cuda()
        model.eval()
    return model


def download_video(video_url, temp_dir):
    # 发送 HTTP GET 请求下载视频
    # stream=True 表示流式传输，不会一次性把文件加载到内存
    response = requests.get(video_url, stream=True)
    response.raise_for_status()

    # Determine file extension from content-type
    content_type = response.headers.get('content-type', '')
    if 'mp4' in content_type:
        ext = '.mp4'
    elif 'avi' in content_type:
        ext = '.avi'
    elif 'mov' in content_type:
        ext = '.mov'
    else:
        ext = '.mp4'  # default

    video_path = os.path.join(temp_dir, f'input_video{ext}')
    with open(video_path, 'wb') as f:
        for chunk in response.iter_content(chunk_size=8192):
            f.write(chunk)
    return video_path


def extract_frames(video_path, output_dir, target_fps):
    """Extract frames from video at specified fps"""
    os.makedirs(output_dir, exist_ok=True)

    cap = cv2.VideoCapture(video_path)
    original_fps = cap.get(cv2.CAP_PROP_FPS)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    print("视频原始fps:{},原始帧率总数：{}".format(original_fps, total_frames))

    # Calculate frame interval
    if target_fps >= original_fps:
        frame_interval = 1
        target_fps = original_fps
    else:
        frame_interval = int(original_fps / target_fps)

    # Estimate total frames to be extracted
    estimated_total = total_frames // frame_interval
    progress_interval = 100
    next_progress = progress_interval

    frame_count = 0
    saved_count = 0
    frame_paths = []

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        if frame_count % frame_interval == 0:
            saved_count += 1
            frame_name = f'{saved_count:05d}.png'
            frame_path = os.path.join(output_dir, frame_name)
            cv2.imwrite(frame_path, frame)
            frame_paths.append(frame_path)

            # Print progress every 300 frames
            if saved_count >= next_progress or saved_count == estimated_total:
                print(f"Extracting frames progress: {saved_count}/{estimated_total}")
                next_progress += progress_interval

        frame_count += 1

    cap.release()
    return frame_paths, original_fps


def extract_audio(video_path, output_path):
    """Extract audio from video using ffmpeg"""
    import subprocess
    cmd = [
        'ffmpeg', '-y', '-i', video_path,
        '-vn', '-acodec', 'pcm_s16le',
        '-ar', '48000', '-ac', '4',  # Try to get 4 channels first
        output_path
    ]
    try:
        subprocess.run(cmd, check=True, capture_output=True)
        return output_path
    except subprocess.CalledProcessError:
        # Fallback to original audio channels
        cmd = [
            'ffmpeg', '-y', '-i', video_path,
            '-vn', '-acodec', 'pcm_s16le',
            '-ar', '48000',
            output_path
        ]
        subprocess.run(cmd, check=True, capture_output=True)
        return output_path


def load_and_process_audio(audio_path, num_frames, clip_length=3):
    """Load audio and split into clips matching video frames

    SoundNet requires minimum audio length ~32768 samples (@48kHz = ~0.68s)
    We collect audio from multiple neighboring clips to ensure sufficient length
    """
    audio, sr = torchaudio.load(audio_path)

    # Get audio info
    num_channels = audio.shape[0]
    total_samples = audio.shape[1]
    sample_rate = sr if sr > 0 else 48000

    # SoundNet needs at least ~32768 samples after all downsampling
    # Use 0.8s minimum duration to be safe
    min_duration_sec = 0.8
    min_samples = int(min_duration_sec * sample_rate)

    # Calculate how many video clips we will have
    num_clips = max(1, (num_frames - 1) // clip_length + 1)

    # Split audio into base clips
    base_samples_per_clip = total_samples // num_clips if num_clips > 0 else total_samples

    audio_clips = []

    # Collect audio from 5 neighboring clips to ensure sufficient length (like original data.py)
    neighbor_window = 2  # +/- 2 clips = 5 clips total

    for i in range(num_clips):
        # Calculate base clip range
        base_start = i * base_samples_per_clip
        base_end = min((i + 1) * base_samples_per_clip, total_samples)

        # Collect from neighboring clips to ensure minimum length
        collect_start = max(0, (i - neighbor_window) * base_samples_per_clip)
        collect_end = min((i + neighbor_window + 1) * base_samples_per_clip, total_samples)

        audio_clip = audio[:, collect_start:collect_end]

        # Pad if still too short
        if audio_clip.shape[1] < min_samples:
            pad_size = min_samples - audio_clip.shape[1]
            audio_clip = torch.nn.functional.pad(audio_clip, (0, pad_size))

        audio_clips.append(audio_clip)

    return audio_clips, num_channels


def cavnet_detect(original_frames_dir, output_dir, audio_path, testsize=416):
    """
    Perform saliency detection using CAVNet

    Args:
        original_frames_dir: Directory containing extracted frames (00001.png, etc.)
        output_dir: Directory to save saliency maps
        audio_path: Path to extracted audio file
        testsize: Input size for model (default 416)
    """
    os.makedirs(output_dir, exist_ok=True)

    # Get all frames
    frame_files = sorted([f for f in os.listdir(original_frames_dir) if f.endswith('.png')])
    if len(frame_files) == 0:
        raise ValueError("No frames found in original_frames_dir")

    # Load model
    model = get_model()

    # Load and process audio
    audio_clips, num_channels = load_and_process_audio(audio_path, len(frame_files))

    # Image transforms
    transform = transforms.Compose([
        transforms.Resize((testsize, testsize * 2)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
    ])

    clip_length = 3
    total_frames = len(frame_files)
    progress_interval = 100 
    next_progress = progress_interval

    # Process frames in clips of 3
    with torch.no_grad():
        for clip_idx in range(len(audio_clips)):
            # Get corresponding frame indices for this clip
            frame_start_idx = clip_idx * clip_length
            frame_end_idx = min(frame_start_idx + clip_length, len(frame_files))

            if frame_end_idx - frame_start_idx == 0:
                break

            # Load and transform frames
            imgs = []
            for i in range(frame_start_idx, frame_end_idx):
                frame_path = os.path.join(original_frames_dir, frame_files[i])
                img = Image.open(frame_path).convert('RGB')
                img_tensor = transform(img).unsqueeze(0).cuda()
                imgs.append(img_tensor)

            # Pad to 3 frames if needed (for last clip)
            while len(imgs) < clip_length:
                imgs.append(imgs[-1])

            # Process audio for this clip
            audio_clip = audio_clips[clip_idx]

            # Handle different audio channel configurations
            if num_channels >= 4:
                # Ambisonics: use first 4 channels
                audio_input = audio_clip[:4, :]
            elif num_channels == 2:
                # Stereo: duplicate to create 4 channels
                audio_input = torch.cat([audio_clip, audio_clip], dim=0)
            elif num_channels == 1:
                # Mono: repeat to create 4 channels
                audio_input = audio_clip.repeat(4, 1)
            else:
                # Other: pad or truncate to 4 channels
                if num_channels < 4:
                    audio_input = torch.cat([audio_clip] * (4 // num_channels + 1), dim=0)[:4, :]
                else:
                    audio_input = audio_clip[:4, :]

            # Reshape audio for CAVNet
            # CAVNet expects audio_clip to be (1, 1, samples), then it does unsqueeze(0) internally
            # to make it (1, 1, 1, samples) for SoundNet
            audio_mean = torch.mean(audio_input, dim=0)  # (samples,)
            audio_input = audio_mean.unsqueeze(0).unsqueeze(0)  # (1, 1, samples)
            audio_input = audio_input.cuda()

            # Run inference (multiple times for uncertainty)
            preds = []
            forward_iter = 5
            for _ in range(forward_iter):
                pred_curr = model(imgs, audio_input)
                for j in range(clip_length):
                    if j < len(preds):
                        preds[j].append(torch.sigmoid(pred_curr[j]))
                    else:
                        preds.append([torch.sigmoid(pred_curr[j])])

            # Average predictions
            for j in range(min(clip_length, frame_end_idx - frame_start_idx)):
                pred_cat = torch.cat(preds[j], dim=1)
                pred_mean = torch.mean(pred_cat, 1, keepdim=True)
                pred_mean = pred_mean.data.cpu().numpy().squeeze()

                # Normalize to [0, 255]
                pred_mean = (pred_mean - pred_mean.min()) / (pred_mean.max() - pred_mean.min() + 1e-8)
                pred_mean = (pred_mean * 255).astype(np.uint8)

                # Resize to original size
                original_frame_path = os.path.join(original_frames_dir, frame_files[frame_start_idx + j])
                original_img = cv2.imread(original_frame_path)
                original_h, original_w = original_img.shape[:2]
                pred_resized = cv2.resize(pred_mean, (original_w, original_h))

                # Save result
                output_name = frame_files[frame_start_idx + j]
                output_path = os.path.join(output_dir, output_name)
                cv2.imwrite(output_path, pred_resized)

                # Print progress every 300 frames
                current_frame = frame_start_idx + j + 1
                if current_frame >= next_progress or current_frame == total_frames:
                    print(f"Processing progress: {current_frame}/{total_frames}")
                    next_progress += progress_interval

    return True


def visualize_results(original_frames_dir, salient_result_dir, output_dir, alpha=0.5):
    """
    将显著性检测结果叠加到原始帧上，显著区域用红色高亮显示

    Args:
        original_frames_dir: 原始帧目录
        salient_result_dir: 显著性检测结果目录
        output_dir: 可视化结果输出目录
        alpha: 红色叠加透明度 (0-1)，默认0.5
    """
    os.makedirs(output_dir, exist_ok=True)

    # 获取所有原始帧文件
    frame_files = sorted([f for f in os.listdir(original_frames_dir) if f.endswith('.png')])

    progress_interval = 100
    next_progress = progress_interval
    total_frames = len(frame_files)

    for i, frame_name in enumerate(frame_files):
        # 读取原始帧
        original_path = os.path.join(original_frames_dir, frame_name)
        original = cv2.imread(original_path)

        # 读取显著性结果
        salient_path = os.path.join(salient_result_dir, frame_name)
        if not os.path.exists(salient_path):
            continue

        salient = cv2.imread(salient_path, cv2.IMREAD_GRAYSCALE)

        # 将显著性图缩放到与原始帧相同大小
        if original.shape[:2] != salient.shape[:2]:
            salient = cv2.resize(salient, (original.shape[1], original.shape[0]))

        # 归一化显著性图到 0-1
        salient_norm = salient.astype(np.float32) / 255.0

        # 创建红色叠加层 (BGR格式: 蓝色=0, 绿色=0, 红色=255)
        red_overlay = np.zeros_like(original)
        red_overlay[:, :] = [0, 0, 255]  # 纯红色

        # 根据显著性值进行加权叠加
        # 显著性高的区域显示更多红色，低显著性区域保持原图
        mask_3channel = np.stack([salient_norm] * 3, axis=-1)
        blended = original * (1 - mask_3channel * alpha) + red_overlay * (mask_3channel * alpha)
        result = blended.astype(np.uint8)

        # 保存结果
        output_path = os.path.join(output_dir, frame_name)
        cv2.imwrite(output_path, result)

        # 打印进度
        current = i + 1
        if current >= next_progress or current == total_frames:
            print(f"Visualizing progress: {current}/{total_frames}")
            next_progress += progress_interval

    print(f"Visualization completed: {total_frames} frames saved to {output_dir}")


def create_video_from_frames(frames_dir, output_path, fps, audio_path=None, codec='avcl'):
    """
    将帧图片合成为视频，可选添加音频

    Args:
        frames_dir: 帧图片所在目录
        output_path: 输出视频路径
        fps: 视频帧率
        audio_path: 音频文件路径（可选）
        codec: 视频编码器，默认 mp4v
    """
    import subprocess

    # 获取所有帧文件并排序
    frame_files = sorted([f for f in os.listdir(frames_dir) if f.endswith('.png')])

    if len(frame_files) == 0:
        raise ValueError(f"No frames found in {frames_dir}")

    # 读取第一帧获取尺寸
    first_frame = cv2.imread(os.path.join(frames_dir, frame_files[0]))
    height, width = first_frame.shape[:2]

    # 创建视频写入器
    fourcc = cv2.VideoWriter_fourcc(*codec)
    out = cv2.VideoWriter(output_path, fourcc, fps, (width, height))

    if not out.isOpened():
        raise RuntimeError(f"Failed to create video writer for {output_path}")

    total_frames = len(frame_files)
    progress_interval = 100
    next_progress = progress_interval

    print(f"Creating video from {total_frames} frames at {fps} fps...")

    for i, frame_name in enumerate(frame_files):
        frame_path = os.path.join(frames_dir, frame_name)
        frame = cv2.imread(frame_path)

        if frame is None:
            print(f"Warning: Failed to read frame {frame_name}")
            continue

        # 确保尺寸一致
        if frame.shape[:2] != (height, width):
            frame = cv2.resize(frame, (width, height))

        out.write(frame)

        # 打印进度
        current = i + 1
        if current >= next_progress or current == total_frames:
            print(f"Creating video progress: {current}/{total_frames}")
            next_progress += progress_interval

    out.release()
    print(f"Video saved to: {output_path}")

    # 如果有音频，合并音频到视频中
    if audio_path and os.path.exists(audio_path):
        print(f"Merging audio from {audio_path}...")
        temp_video_path = output_path.replace('.mp4', '_temp.mp4')
        os.rename(output_path, temp_video_path)

        # 使用 ffmpeg 合并音频和视频
        cmd = [
            'ffmpeg', '-y',
            '-i', temp_video_path,  # 视频输入
            '-i', audio_path,       # 音频输入
            '-c:v', 'copy',         # 视频流直接复制
            '-c:a', 'aac',          # 音频编码为 AAC
            '-strict', 'experimental',
            output_path
        ]

        try:
            subprocess.run(cmd, check=True, capture_output=True)
            os.remove(temp_video_path)  # 删除临时视频文件
            print(f"Audio merged successfully to: {output_path}")
        except subprocess.CalledProcessError as e:
            # 如果合并失败，保留无声视频
            print(f"Warning: Failed to merge audio: {e}")
            if os.path.exists(temp_video_path):
                os.rename(temp_video_path, output_path)
            print(f"Kept video without audio: {output_path}")


def detect(video_url, fps):

    if not video_url:
        return {"error": "Missing required parameter: url"}, 400

    if not fps:
        return {"error": "Missing required parameter: fps"}, 400

    try:
        if fps <= 0:
            return {"error": "fps must be positive integer"}, 400
    except ValueError:
        return {"error": "fps must be integer"}, 400

    #创建保存目录
    original_frames_dir = "original_frames"
    salient_result_dir = "salient_result"
    visualization_dir = "visualization_result"
    os.makedirs(original_frames_dir, exist_ok=True)
    os.makedirs(salient_result_dir, exist_ok=True)
    os.makedirs(visualization_dir, exist_ok=True)

    #清除以前结果
    for f in os.listdir(original_frames_dir):
        os.remove(os.path.join(original_frames_dir, f))
    for f in os.listdir(salient_result_dir):
        os.remove(os.path.join(salient_result_dir, f))
    for f in os.listdir(visualization_dir):
        os.remove(os.path.join(visualization_dir, f))

    temp_dir = tempfile.mkdtemp()

    try:
        # Step 1: 根据url下载视频
        print(f"Downloading video from {video_url}")
        video_path = download_video(video_url, temp_dir)

        # Step 2: 根据给定的fps提取视频帧
        print(f"Extracting frames at {fps} fps")
        frame_paths, original_fps = extract_frames(video_path, original_frames_dir, fps)
        print(f"Extracted {len(frame_paths)} frames")

        # Step 3: 提取音频文件
        print("Extracting audio")
        audio_path = os.path.join(temp_dir, "audio.wav")
        extract_audio(video_path, audio_path)

        # Step 4: 开始显著性检测
        print("Running saliency detection")
        cavnet_detect(original_frames_dir, salient_result_dir, audio_path)

        # Step 5: 生成可视化结果
        print("Generating visualization")
        visualization_dir = "visualization_result"
        visualize_results(original_frames_dir, salient_result_dir, visualization_dir, alpha=0.5)

        # Step 6: 将可视化帧合成为视频（添加音频）
        print("Creating output video with audio")
        output_video_path = "output_saliency_video.mp4"
        create_video_from_frames(visualization_dir, output_video_path, fps, audio_path=audio_path)

        # 获取处理后视频的时长和大小
        output_cap = cv2.VideoCapture(output_video_path)
        output_total_frames = int(output_cap.get(cv2.CAP_PROP_FRAME_COUNT))
        output_fps = output_cap.get(cv2.CAP_PROP_FPS)
        output_duration = output_total_frames / output_fps if output_fps > 0 else 0
        output_cap.release()

        output_file_size = os.path.getsize(output_video_path)

        print(f"Output video: duration={output_duration:.2f}s, size={output_file_size} bytes")

        # Step 7: 上传检测后的视频及其封面到阿里云 OSS
        print("Uploading video to Aliyun OSS")
        with open(output_video_path, 'rb') as f:
            video_content = f.read()
        salient_url, fileName = aliyunOssUpload(video_content, os.path.basename(output_video_path))
        print(f"Video uploaded to: {salient_url}")

        cover_image_path = "visualization_result/00001.png"
        with open(cover_image_path, 'rb') as f:
            cover_image_content = f.read()
        salient_cover_url, coverName = aliyunOssUpload(cover_image_content, os.path.basename(cover_image_path))

        # Step 8：向salient_video表插入一条数据
        #连接MySQL数据库
        conn = pymysql.connect(host=opts.database_opt.get("host"),
                            port=opts.database_opt.get("port"),
                            user=opts.database_opt.get("user"),
                            passwd=opts.database_opt.get("passwd"),
                            db = opts.database_opt.get("db")
                            )
        cursor = conn.cursor()

        cursor.execute("select video_id from original_video where video_url = %s", (video_url,))
        original_video_id = cursor.fetchone()[0]

        insert_salient_sql = """
        INSERT INTO salient_video 
        (original_video_id, video_name, duration, video_size, video_url, video_cover)
        VALUES (%s, %s, %s, %s, %s, %s)
        """

        cursor.execute(
            insert_salient_sql, 
            (original_video_id, fileName, output_duration, output_file_size, salient_url, salient_cover_url)
        )
        conn.commit()


        cursor.execute("select salient_video_id from salient_video where video_url = %s", (salient_url,))
        salient_id = cursor.fetchone()[0]
        print(f"salient_video_id: {salient_id}")

        update_task_salient_video_id_sql = """
        UPDATE task 
        SET salient_video_id = %s, task_status = 2, end_time = NOW()
        WHERE original_video_id = %s
        """
        cursor.execute(update_task_salient_video_id_sql, (salient_id, original_video_id))
        conn.commit()

        # Step 9: 获取AI评价
        print("Getting AI evaluation")
        ai_suggestion = llm_suggest(salient_url)
        print(f"AI evaluation: {ai_suggestion}")
        cursor.execute("update task set ai_suggestion = %s where original_video_id = %s", (ai_suggestion, original_video_id))
        conn.commit()

        cursor.close()
        conn.close()

        return {
            "status": "success",
            "frames_extracted": len(frame_paths),
            "original_fps": original_fps,
            "target_fps": fps,
            "original_frames_dir": original_frames_dir,
            "salient_result_dir": salient_result_dir,
            "visualization_dir": visualization_dir,
            "output_video_path": output_video_path,
            "salient_url": salient_url,
            "output_duration": round(output_duration, 2),
            "output_file_size": output_file_size
        }, 200

    except Exception as e:
        print(e)
        return {"error": str(e)}, 500

    finally:
        # Cleanup temp directory
        if os.path.exists(temp_dir):
            shutil.rmtree(temp_dir)



def get_task():
    conn = redis.Redis(
                        host=opts.redis_opt.get("host"),
                        port=opts.redis_opt.get("port"),
                        password=opts.redis_opt.get("password"),
                        encoding=opts.redis_opt.get("encoding")
                        )
    data = conn.brpop("pavsod_task_list", timeout=10)
    if data is None:
        return None
    return json.loads(data[1].decode('utf-8'))

def run():
    while(True):
        task_dict = get_task()
        if not task_dict or task_dict == None:
            continue
        
        #{"url" : "xxx", "fps": 30}
        url = task_dict.get("url")
        fps = task_dict.get("fps")
        print("当前处理视频url：{}，fps：{}".format(url, fps))

        #连接MySQL数据库
        conn = pymysql.connect(host=opts.database_opt.get("host"),
                            port=opts.database_opt.get("port"),
                            user=opts.database_opt.get("user"),
                            passwd=opts.database_opt.get("passwd"),
                            db = opts.database_opt.get("db")
                            )
        cursor = conn.cursor()

        #根据 url 查询 original_video_id,修改状态为正在处理
        sql1 = "select video_id from original_video where video_url = %s"
        cursor.execute(sql1,(url,))
        result = cursor.fetchone()
        if not result:
            continue

        original_video_id = result[0]
        print(f"开始处理视频:{original_video_id}")

        sql2 = "update task set task_status=1, start_time = NOW() where original_video_id = %s"
        cursor.execute(sql2, (original_video_id,))
        conn.commit()

        try:
            result, _ = detect(url, fps)
            if result.get("status") == "success":
                sql4 = "update task set task_status=2, end_time = NOW() where original_video_id=%s"
                cursor.execute(sql4, (original_video_id))
                conn.commit()
            else:
                raise Exception(result.get("error", "Unknown error"))

        except Exception as e:
            print(e)

            #修改task状态为异常
            sql3 = "update task set task_status=3, end_time = NOW() where original_video_id=%s"
            cursor.execute(sql3, (original_video_id,))
            conn.commit()

            error_msg = str(e)
            sql5 = "update task set error_msg = %s where original_video_id=%s"
            cursor.execute(sql5, (error_msg, original_video_id))
            conn.commit()


        finally:
            cursor.close()
            conn.close()
    

if __name__ == '__main__':
    run()