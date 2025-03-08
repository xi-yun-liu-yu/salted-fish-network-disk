package com.xiyun.saltedfishnetdish.controller;

import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.utils.AliOssUtil;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {


    @PostMapping({"/api/file/upload"})
    public Result<String> upload(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String var10000 = UUID.randomUUID().toString();
        String filename = var10000 + originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = AliOssUtil.uploadFile(filename, file.getInputStream());
        return Result.success(url);
    }
}

