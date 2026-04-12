package com.pavsod.pavsodbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    public static String APP_NAME;
    public static String APP_VERSION;
    public static String OSS_ENDPOINT;
    public static String OSS_BUCKET_NAME;
    public static String OSS_REGION;
    public static String DEFAULT_PHOTO_URL;
    public static String DEFAULT_JWT_SECRET;
    public static String FLASK_HOST;
    public static Integer FLASK_PORT;

    @Value("${app.name}")
    private void setAppName(String name){
        APP_NAME = name;
    }

    @Value("${app.version}")
    private void setAppVersion(String version){
        APP_VERSION = version;
    }

    @Value("${app.oss.endpoint}")
    private void setOssEndpoint(String endpoint){
        OSS_ENDPOINT = endpoint;
    }

    @Value("${app.oss.bucketName}")
    private void setOssBucketName(String bucketName){
        OSS_BUCKET_NAME = bucketName;
    }

    @Value("${app.oss.region}")
    private void setOssRegion(String region){
        OSS_REGION = region;
    }

    @Value("${app.default.photo_url}")
    private void setDefaultPhotoUrl(String defaultPhotoUrl){
        DEFAULT_PHOTO_URL = defaultPhotoUrl;
    }

    @Value("${app.default.jwt_secret}")
    private void setDefaultJwtSecret(String defaultJwtSecret){
        DEFAULT_JWT_SECRET = defaultJwtSecret;
    }

    @Value("${app.flask.host}")
    private void setFlaskHost(String flaskHost){
        FLASK_HOST = flaskHost;
    }

    @Value("${app.flask.port}")
    private void setFlaskPort(Integer flaskPort){
        FLASK_PORT = flaskPort;
    }
}
