package com.xiyun.saltedfishnetdish.service.impl;

import com.xiyun.saltedfishnetdish.dao.FileMapper;
import com.xiyun.saltedfishnetdish.pojo.File;
import com.xiyun.saltedfishnetdish.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public int insertFile(File file) {
        return fileMapper.insertFile(file);
    }

    @Override
    public int deleteFileById(String id) {
        return fileMapper.deleteFileById(id);
    }

    @Override
    public int updateFile(File file) {
        return fileMapper.updateFile(file);
    }

    @Override
    public File selectFileById(String id) {
        return fileMapper.selectFileById(id);
    }

    @Override
    public List<File> selectAllFiles() {
        return fileMapper.selectAllFiles();
    }
}