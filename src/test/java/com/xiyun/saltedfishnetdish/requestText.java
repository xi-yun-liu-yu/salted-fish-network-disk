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
        AliOssUtil.downLoadByUrl("http://eyJjbGFpbXMiOnsidXJsIjoic2ZuZC5vc3MtY24tY2hlbmdkdS5hbGl5dW5jcy5jb20vMjFlNjQ4MDEtODkzZS00YjBmLTllYzAtMDFhOTdjMjBjNDkxLmpwZz9FeHBpcmVzPTE3NDE1OTY1MjYmT1NTQWNjZXNzS2V5SWQ9TFRBSTV0R0xTenVxeGJyWmtabTRRYzRDJlNpZ25hdHVyZT1IbTN4M2NPbDNDQTMlMkJyTnhwakZ5Z3B6UVVxbyUzRCJ9LCJleHAiOjE3NDE2MzYxMjZ9.ToPJdxOTCJGVuM2cIu0y8NFaw4OzZ0XEs1lGsf1w5rg","E:/a.jpg");
    }
}
