import os
from openai import OpenAI
import global_options as opts


def llm_suggest(url):
    client = OpenAI(
    # 若没有配置环境变量，请用阿里云百炼API Key将下行替换为：api_key="sk-xxx",
    # 各地域的API Key不同。获取API Key：https://help.aliyun.com/zh/model-studio/get-api-key
    api_key=opts.llm_opt["api_key"],
    # 各地域配置不同，请根据实际地域修改
    base_url=opts.llm_opt["base_url"],
    )

    completion = client.chat.completions.create(
        model="qwen3.6-plus", # 此处以qwen3.6-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/models
        messages=[
            {
                "role": "user",
                "content": [
                    {
                        "type": "video_url",
                        "video_url": {
                            "url": url
                        },
                    },
                    {"type": "text", "text": "这是一个经过显著性检测后的视频，\
                    标红的部分是我通过模型检测出来的显著部分\
                    请根据视频内容给显著性结果做一个小总结，无关检测的模型，只面向提出检测的用户。\
                    要求：1.简介明了不要超过300字数\
                    2.先总结视频内容，再给出检测结果和建议\
                    3.使用文本语法，不要使用markdown语法"},
                ],
            },
        ],
    )
    print(completion.choices[0].message.content)
    return completion.choices[0].message.content
if __name__ == "__main__":
    llm_suggest("https://pavsod-oss.oss-cn-beijing.aliyuncs.com/ca29a980-3184-40e4-b566-aedc3c85b559output_saliency_video.mp4")