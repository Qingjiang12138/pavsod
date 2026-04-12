# 基于音频信息的视觉显著物体检测设计与实现
## 毕业设计：Qingjiang Li（Hefei University of Technology）
前端：Vue

后端：SpringBoot + Flask

数据库：MySQL + Redis

显著性检测模型：[CAV-Net](https://github.com/YeeZ93/PAV-SOD)（TOMM 2022）


## 前端运行：

```sh
cd pavsod_front
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Type-Check, Compile and Minify for Production

```sh
npm run build
```



## Flask框架运行CAV-Net

请注意，该项目需要自行准备阿里云OSS存储服务，请自行准备region，endpoint，bucketName等基本阿里云OSS配置

### 下载预训练模型
在开始准备显著性检测之前，请先[下载预训练模型](https://drive.google.com/file/d/1gNWmgmlBfJqCYE5phDuHTMFIou1TAmXs/view)。并且解压后放在 pavsod\CAVNet-Flask\src 目录下。

正确模型权重位置应该如下：

pavsod\CAVNet-Flask\src\CAV-Net_models\final\CAVNet_final.pth

pavsod\CAVNet-Flask\src\CAV-Net_models\pretrain\soundnet8.pth

pavsod\CAVNet-Flask\src\CAV-Net_models\pretrain\static_visual_pretrain.pth

将目录转到CAVNet-Flask下：
```sh
cd CAVNet-Flask
```

### 准备环境：
```sh
conda create -n cavnet python=3.13
conda activate cavnet

pip install -r requirements.txt
```

### 配置必要变量
打开CAVNet-Flask下的global_options.py文件
根据自己的实际情况，修改数据库配置，Flask框架相关配置，Redis相关配置，阿里云OSS相关配置

### 启动Flask框架：
```sh
python flask_backend.py
```

在另一个终端中启动worker.py:
```sh
python worker.py
```

## SpringBoot后端运行：

### 进入pavsod-backend目录下：
```sh
cd pavsod-backend
```

使用IDEA打开pavsod-backend项目，通过Maven加载pom.xml中的项目依赖

在pavsod-backend\src\main\resources目录创建application.yaml文件进行项目配置
```sh
app:
  name: "PAV-SOD全景级音视频显著性检测系统"
  version: "1.0.0"
  #阿里云存储配置
  oss:
    endpoint: #"your aliyun oss endpoint"
    bucketName: #"your aliyun oss bucketName"
    region: #"your aliyun oss region"
  default:
    photo_url: #"user default photo url"
    jwt_secret: #"your jwt secret"
  # 根据flask框架的配置进行配置
  flask:
    host: localhost
    port: 5000

server:
  port: 2778

# DataSource Config
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pavsod_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: #数据库username
    password: #数据库密码
  servlet:
    multipart:
      max-file-size: 1000MB
      max-request-size: 1000MB

mybatis:
  mapper-locations: classpath:mapper/*.xml

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: false  # 关闭自动驼峰转换

```

在IDEA中,通过启动类启动SpringBoot项目