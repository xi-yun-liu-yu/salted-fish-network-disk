package com.xiyun.saltedfishnetdish.service;

import com.xiyun.saltedfishnetdish.pojo.FileNode;

import java.util.List;

public interface FileService {
    FileNode addNode(FileNode fileNode);
    FileNode getNodeById(String id);
    List<FileNode> getAllNodes();
    List<FileNode> getChildrenByParentId(String parentId);
    FileNode updateNodeName(String id, String newName);
    FileNode updateNodeParent(String id, String newParentId);
    void deleteNode(String id);
    void deleteNodeRecursively(String id);
}
