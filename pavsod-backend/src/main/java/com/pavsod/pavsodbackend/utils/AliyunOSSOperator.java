package com.pavsod.pavsodbackend.utils;

import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.pavsod.pavsodbackend.config.AppConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AliyunOSSOperator {

    // 修正：去掉endpoint中的空格
    private String endpoint = AppConfig.OSS_ENDPOINT;
    private String bucketName = AppConfig.OSS_BUCKET_NAME;
    private String region = AppConfig.OSS_REGION;

    public String upload(InputStream inputStream, String objectName) throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider =
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            // 创建PutObject请求
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 返回文件的完整访问URL
            return getFileUrl(objectName);

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw oe; // 抛出异常让上层处理
        } catch (ClientException ce) {
            System.out.println("Caught a ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw ce; // 抛出异常让上层处理
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 生成文件的完整访问URL
     */
    private String getFileUrl(String objectName) {
        // 格式: https://bucketName.endpoint/objectName
        // 或者: https://endpoint/bucketName/objectName (取决于OSS配置)

        // 方式1：标准URL格式（推荐）
        return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;

        // 方式2：如果使用了自定义域名，可能需要这种格式
        // return endpoint + "/" + bucketName + "/" + objectName;
    }
}