package com.xiyun.saltedfishnetdish.controller;

import com.xiyun.saltedfishnetdish.pojo.FileNode;
import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.service.FileNodeService;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.AliOssUtil;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FileNodeService fileNodeService;

    //上传文件到服务器
    @PostMapping({"/api/file/upload"})
    public Result<String> upload(MultipartFile file, @RequestBody FileNode fileNode) throws Exception {
        String url;
        try{
            String userStorage = stringRedisTemplate.opsForValue().get("userStorage");
            String userStorageLimit = stringRedisTemplate.opsForValue().get("userStorageLimit");
            Long value = file.getSize();
            System.out.println(value);
            String storage;

            if (userStorage != null && userStorageLimit != null) {
                System.out.println((Long.parseLong(userStorage) + value +" "+ Long.parseLong(userStorageLimit)));
                if (Long.parseLong(userStorage) + value <= Long.parseLong(userStorageLimit)){
                    storage = String.valueOf(Long.parseLong(userStorage) + value);
                }else {
                    return Result.error("未使用的空间不足");
                }

                userService.userStorage(Long.parseLong(storage));
                stringRedisTemplate.opsForValue().set("userStorage", storage);
            }
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
            List<String> children = fileNode.getChildren();
            children.add(filename);
            fileNode.setChildren(children);
            fileNodeService.addNode(new FileNode("filename","originalFilename","file",fileNode.getId(),null));
            url = AliOssUtil.uploadFile(filename, file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.success(url);
    }
    //从服务器下载文件到本地指定地址
    @GetMapping({"/api/file/downLoad"})
    public Result downLoad(String fileName,String filePath) throws Exception {
        AliOssUtil.downLoad(fileName,filePath);
        return Result.success("下载成功");
    }

    //分享文件

    //更新文件元数据

    //获取文件元数据

    //

}

