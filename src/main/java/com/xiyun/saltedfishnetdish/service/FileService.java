package com.xiyun.saltedfishnetdish.service;

import com.xiyun.saltedfishnetdish.pojo.File;

import java.util.List;

public interface FileService {

    // 插入文件记录
    int insertFile(File file);

    // 根据 ID 删除文件记录
    int deleteFileById(Long id);

    // 更新文件记录
    int updateFile(File file);

    // 根据 ID 查询文件记录
    File selectFileById(Long id);

    // 查询所有文件记录
    List<File> selectAllFiles();
}