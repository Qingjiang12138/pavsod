from flask import Flask, request
import glogal_options as opts
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

app = Flask(__name__)

@app.route("/task", methods = ["POST"])
def task():
    print("进入task")
    task_dict = {
        'url': request.get_json().get("url"),
        'fps': request.get_json().get("fps")
    }
    conn = redis.Redis(
                        host=opts.redis_opt.get("host"),
                        port=opts.redis_opt.get("port"),
                        password=opts.redis_opt.get("password"),
                        encoding=opts.redis_opt.get("encoding")
                        )
    conn.lpush("pavsod_task_list", json.dumps(task_dict))
    
    return {"status" : True, 'message': "已加入任务队列"}

#json格式传递数据
@app.route("/xxx", methods = ["POST", "GET"])
def index():

    #连接MySQL数据库
    conn = pymysql.connect(host=opts.database_opt.get("host"),
                           port=opts.database_opt.get("port"),
                           user=opts.database_opt.get("user"),
                           passwd=opts.database_opt.get("passwd"),
                           db = opts.database_opt.get("db")
                           )
    cursor = conn.cursor()

    cursor.execute("select * from user")
    result = cursor.fetchall()
    print(result)
    cursor.close()
    conn.close()

    print(request.json)
    return "success"



#表单形式传入数据
@app.route("/yyy")
def home():
    age = request.args.get("age")
    pwd = request.args.get("pwd")
    return "fail"

if __name__ == '__main__':
    app.run(host = opts.global_opt.get("host"), port=opts.global_opt.get("port"))

