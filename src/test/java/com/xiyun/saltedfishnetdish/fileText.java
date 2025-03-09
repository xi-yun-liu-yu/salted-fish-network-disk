package com.xiyun.saltedfishnetdish;

import com.xiyun.saltedfishnetdish.pojo.FileNode;
import com.xiyun.saltedfishnetdish.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class fileText {
    @Autowired
    private FileService fileService;

    @Test
    public void fileAddText() {
        fileService.addNode(new FileNode("rootid","root","folder",null,null));
    }
}
