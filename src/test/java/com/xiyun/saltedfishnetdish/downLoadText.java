package com.xiyun.saltedfishnetdish;


import com.xiyun.saltedfishnetdish.controller.FileUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class downLoadText {
    @Autowired
    private FileUploadController fileUploadController;
    @Test
    public void downLoadt() throws Exception {
        fileUploadController.downLoad("21e64801-893e-4b0f-9ec0-01a97c20c491.jpg","E:\\a.jpg");
    }
}
