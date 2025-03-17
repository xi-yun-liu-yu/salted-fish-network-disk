package com.xiyun.saltedfishnetdish.controller;

import com.alibaba.fastjson.JSON;
import com.xiyun.saltedfishnetdish.pojo.File;
import com.xiyun.saltedfishnetdish.pojo.FileNode;
import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.service.FileNodeService;
import com.xiyun.saltedfishnetdish.service.FileService;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.AliOssUtil;
import java.text.SimpleDateFormat;
import java.util.*;
import com.xiyun.saltedfishnetdish.utils.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FileNodeService fileNodeService;
    @Autowired
    private FileService fileService;

    //上传文件到服务器
    @Transactional
    @PostMapping({"/api/file/upload/{nodeId}/{originalFilename}"})
    public Result<String> upload(MultipartFile file, @PathVariable String nodeId, @PathVariable String originalFilename) throws Exception {
        String url;

        try{
            String userStorage = stringRedisTemplate.opsForValue().get("userStorage");
            String userStorageLimit = stringRedisTemplate.opsForValue().get("userStorageLimit");
            Long value = file.getSize();

            String storage;

            if (userStorage != null && userStorageLimit != null) {
                if (Long.parseLong(userStorage) + value <= Long.parseLong(userStorageLimit)){
                    storage = String.valueOf(Long.parseLong(userStorage) + value);
                }else {
                    return Result.error("未使用的空间不足");
                }

                userService.userStorage(Long.parseLong(storage));
                stringRedisTemplate.opsForValue().set("userStorage", storage);
            }
//            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
            FileNode nodeById = fileNodeService.getNodeById(nodeId);
            String fileTypeAfter = StringUtils.substringAfter(file.getContentType(), "/");
            String fileTypeBefore = StringUtils.substringBefore(file.getContentType(), "/");
            List<String> children = nodeById.getChildren();
            List<FileNode> childrenByParentId = fileNodeService.getChildrenByParentId(nodeId);
            for(FileNode child : childrenByParentId){
                if (child.getName().equals(originalFilename) ){
                    originalFilename = originalFilename.substring(0,originalFilename.lastIndexOf(".")) + "~" + originalFilename.substring(originalFilename.lastIndexOf("."));
                }
            }
            children.add(filename);
            fileNodeService.updateNodeChildren(nodeId, children);

            url = AliOssUtil.uploadFile(filename, file.getInputStream());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 获取当前时间
            Date now = new Date();
            // 将时间格式化为指定格式的字符串
            String timeStr = sdf.format(now);

            fileNodeService.addNode(new FileNode(filename,originalFilename,fileTypeBefore,url,file.getSize(),nodeId,null,timeStr));
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String)map.get("username");
            Integer userId = (Integer)map.get("userId");
            File file1 = new File(originalFilename,filename, fileTypeAfter,file.getSize(),0,url,"private",userId,username,null,null);
            fileService.insertFile(file1);

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
    @GetMapping({"/api/file/shareUrl/{fileUUid}"})
    public Result shareFile(@PathVariable String fileUUid) throws Exception {
        String s = AliOssUtil.shareUrl(fileUUid);
        return Result.success(s);
    }

    //使用分享链接下载文件
    @PostMapping({"/api/file/getFileByShareUrl"})
    public Result getShareFile(@RequestParam String shareUrl,@RequestParam String fileSavePath) throws Exception {
        AliOssUtil.downLoadByUrl(shareUrl,fileSavePath);
        return Result.success(fileSavePath);
    }

    //更新文件元数据
    @PutMapping({"/api/file/updateFileTable"})
    public Result updateFileTable(@RequestBody File file){
        File file1 = fileService.selectFileById(file.getFileUuid());
        if(file1 == null){
            return Result.error("此文件不存在");
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        if (file1.getViewPermission().equals("private") && userId == file1.getCreatorId()){
            return Result.error("无操作权限");
        }

        if (file.getFileUuid().equals(file1.getFileUuid())){
            fileService.updateFile(file);
            return Result.success("更新成功");
        }
        return Result.error("文件id不一致");
    }

    //获取文件元数据
    @PostMapping({"/api/file/getFileTable"})
    public Result getFileTable(@RequestBody List<String> children){
        List<File> files = new ArrayList<>();
        for(String child : children){
            files.add(fileService.selectFileById(child));
        }
        return Result.success(JSON.toJSONString(files));
    }

    //获取文件节点
    @PostMapping({"/api/file/getFileNode"})
    public Result getFileNode(@RequestBody List<String> children){
        List<FileNode> fileNodes = new ArrayList<>();
        for(String child : children){
            fileNodes.add(fileNodeService.getNodeById(child));
        }
        System.out.println(JSON.toJSONString(fileNodes));
        return Result.success(JSON.toJSONString(fileNodes));
    }

    //删除文件
    @DeleteMapping({"/api/file/deleteFile/{fileUuid}"})
    public Result deleteFile(@PathVariable String fileUuid) throws Exception {
        File file1 = fileService.selectFileById(fileUuid);
        if(file1 == null || file1.getFileFormat().equals( "folder")){
            return Result.error("此文件不存在");
        }
        String parentId = fileNodeService.getNodeById(fileUuid).getParentId();
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        if (file1.getViewPermission().equals("private") && userId == file1.getCreatorId()){
            return Result.error("无操作权限");
        }
        AliOssUtil.deleteFile(fileUuid);
        List<String> children = fileNodeService.getNodeById(parentId).getChildren();
        children.remove(fileUuid);
        fileNodeService.updateNodeChildren(parentId, children);
        fileService.deleteFileById(fileUuid);
        fileNodeService.deleteNode(fileUuid);
        return Result.success("删除成功");
    }


    //创建文件夹
    @Transactional
    @PostMapping({"/api/file/createFolder/{folderName}/{nodeId}"})
    public Result createFolder(@PathVariable String folderName, @PathVariable String nodeId){
        try{
            String uuid = UUID.randomUUID().toString();
            FileNode nodeById = fileNodeService.getNodeById(nodeId);
            List<String> children = nodeById.getChildren();
            List<FileNode> childrenByParentId = fileNodeService.getChildrenByParentId(nodeId);
            for(FileNode child : childrenByParentId){
                if (child.getName().equals(folderName) && child.getType().equals("folder")){
                    folderName = folderName + "~";
                }
            }
            children.add(uuid);
            fileNodeService.updateNodeChildren(nodeId, children);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 获取当前时间
            Date now = new Date();
            // 将时间格式化为指定格式的字符串
            String timeStr = sdf.format(now);
            fileNodeService.addNode(new FileNode(uuid,folderName,"folder",null,0L,nodeId,null,timeStr));
            Map<String, Object> map = ThreadLocalUtil.get();
            String username = (String)map.get("username");
            Integer userId = (Integer)map.get("userId");
            File file1 = new File(folderName, uuid, "folder",0,0,"","private",userId,username,null,null);
            fileService.insertFile(file1);
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
        return Result.success("已创建" + folderName);
    }
    @GetMapping({"/api/file/getFileNodeTree"})
    public Result getFileNodeTree(){
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        return Result.success(fileNodeService.getTree(String.valueOf(userId)));
    }

    //删除指定文件夹下的树
    public void deleteFile(){

    }
}

