package com.xiyun.saltedfishnetdish;


import com.xiyun.saltedfishnetdish.controller.FileUploadController;
import com.xiyun.saltedfishnetdish.utils.AliOssUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class requestText {
    @Autowired
    private FileUploadController fileUploadController;
    @Test
    public void downLoadt() throws Exception {
        fileUploadController.downLoad("21e64801-893e-4b0f-9ec0-01a97c20c491.jpg","E:\\a.jpg");
    }
    @Test
    public void getShareUrl() throws Exception {
//        System.out.println(AliOssUtil.shareUrl("1986db27-f192-4e09-8d5a-adb5bed46bdc.jpg"));
        System.out.println(AliOssUtil.shareUrl("21e64801-893e-4b0f-9ec0-01a97c20c491.jpg"));
    }

    @Test
    public void downByShareUrl() throws Exception {
        AliOssUtil.deleteFile("cf163ccb-1b24-47ac-9061-d89d75a334a8.jpg");
    }
}
