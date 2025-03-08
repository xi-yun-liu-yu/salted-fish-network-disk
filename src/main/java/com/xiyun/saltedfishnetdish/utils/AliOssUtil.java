//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiyun.saltedfishnetdish.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import java.io.InputStream;

public class AliOssUtil {
    private static final String ENDPOINT = "https://oss-cn-chengdu.aliyuncs.com";
    private static final String BUCKET_NAME = "sfnd";

    public AliOssUtil() {
    }

    public static String uploadFile(String objectName, InputStream in) throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = (new OSSClientBuilder()).build("https://oss-cn-chengdu.aliyuncs.com", credentialsProvider);
        String url = "";

        try {
            String content = "Hello OSS，你好世界";
            PutObjectRequest putObjectRequest = new PutObjectRequest("sfnd", objectName, in);
            ossClient.putObject(putObjectRequest);
            String var10000 = "https://oss-cn-chengdu.aliyuncs.com".substring("https://oss-cn-chengdu.aliyuncs.com".lastIndexOf("/") + 1);
            url = "https://sfnd." + var10000 + "/" + objectName;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered a serious internal problem while trying to communicate with OSS, such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }

        }

        return url;
    }
}
