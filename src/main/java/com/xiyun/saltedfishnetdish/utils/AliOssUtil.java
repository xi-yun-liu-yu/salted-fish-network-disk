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
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AliOssUtil {
    private static final String ENDPOINT = "oss-cn-chengdu.aliyuncs.com";
    private static final String BUCKET_NAME = "sfnd";


    public AliOssUtil() {
    }

    public static String uploadFile(String objectName, InputStream in) throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = (new OSSClientBuilder()).build(ENDPOINT, credentialsProvider);
        String url = "";

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, objectName, in);
            ossClient.putObject(putObjectRequest);
            url = "https://sfnd." + ENDPOINT + "/" + objectName;
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

    public static void downLoad(String objectName, String filePath) throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = (new OSSClientBuilder()).build(ENDPOINT, credentialsProvider);
        String url = "";

        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, objectName);
            ossClient.getObject(getObjectRequest, new File(filePath));
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
    }

    public static String shareUrl(String objectName) throws Exception {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = (new OSSClientBuilder()).build(ENDPOINT, credentialsProvider);
        String url = "";
        try {
            // 设置预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
            // 生成以GET方法访问的预签名URL。本示例没有额外请求头，其他人可以直接通过浏览器访问相关内容。
            url = String.valueOf(ossClient.generatePresignedUrl(BUCKET_NAME, objectName, expiration));
            System.out.println(url);
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("url",StringUtils.substringAfter(url, "http://"));
            System.out.println(StringUtils.substringAfter(url, "http://"));
            System.out.println(stringObjectMap.get("url"));
            url = "http://" + StringUtils.substringAfter(JwtUtil.genToken(stringObjectMap),"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.");

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

    public static void downLoadByUrl(String fileURL, String fileSavePath) throws Exception {
        try {
            Map<String, Object> stringObjectMap = JwtUtil.parseToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + StringUtils.substringAfter(fileURL, "http://"));
            System.out.println(stringObjectMap);
            fileURL = "http://" + stringObjectMap.get("url");
            System.out.println(fileURL);
            downloadFile(fileURL, fileSavePath);

        } catch (IOException e) {
            System.err.println("Error during download: " + e.getMessage());
        }
    }

    private static void downloadFile(String fileURL, String savePath) throws IOException {
        URL url = new URL(fileURL);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        // 检查响应代码
        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 输入流
            InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
            // 输出流
            FileOutputStream outputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[4096]; // 缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
}
