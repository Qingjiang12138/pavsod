#MySQL数据库相关配置
database_opt = {
    "host": '127.0.0.1',
    "port": 3306,
    "user": '', #your dataset user
    "passwd": "",#your dataset password
    "charset": "utf8",
    "db": 'pavsod_db'
}

#Flask框架相关配置
global_opt = {
    "host": '127.0.0.1',
    "port": 5000
}

#Redis相关配置
redis_opt = {
    "host": '127.0.0.1',
    "password": "",
    "port": 6379,
    "encoding": 'utf-8'
}

#阿里云OSS相关配置
oss_opt = {
    "endpoint": "https://oss-cn-beijing.aliyuncs.com",
    "bucketName": "pavsod-oss",
    "region": "cn-beijing"
}

llm_opt = {
    "api_key": "",#your aliyun api key
    "base_url": ""#your aliyun base url
}