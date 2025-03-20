package com.xiyun.saltedfishnetdish.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.annotations.JsonAdapter;
import com.xiyun.saltedfishnetdish.pojo.FileNode;
import com.xiyun.saltedfishnetdish.service.FileNodeService;
import com.xiyun.saltedfishnetdish.service.FileService;
import com.xiyun.saltedfishnetdish.utils.AliOssUtil;
import com.xiyun.saltedfishnetdish.utils.MongoDBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileNodeServiceImpl implements FileNodeService {

    @Autowired
    private MongoDBUtil mongoDBUtil;
    @Autowired
    private FileService fileService;

    private static final String COLLECTION_NAME = "files";

    /**
     * 添加文件或文件夹节点
     *
     * @param fileNode 文件或文件夹节点
     * @return 添加后的节点
     */
    public FileNode addNode(FileNode fileNode) {
        // 如果是文件夹节点，初始化children为空列表
        if ("folder".equals(fileNode.getType())) {
            fileNode.setChildren(List.of());
        } else {
            fileNode.setChildren(null); // 文件节点没有children
        }
        return mongoDBUtil.insert(fileNode, COLLECTION_NAME);
    }

    /**
     * 根据ID查询节点
     *
     * @param id 节点ID
     * @return 查询到的节点
     */
    public FileNode getNodeById(String id) {
        return mongoDBUtil.findById(id, FileNode.class, COLLECTION_NAME);
    }

    /**
     * 查询所有节点
     *
     * @return 所有节点列表
     */
    public List<FileNode> getAllNodes() {
        return mongoDBUtil.findAll(FileNode.class, COLLECTION_NAME);
    }

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    public List<FileNode> getChildrenByParentId(String parentId) {
        Query query = new Query(Criteria.where("parentId").is(parentId));
        return mongoDBUtil.findByQuery(query, FileNode.class, COLLECTION_NAME);
    }

    /**
     * 更新节点名称
     *
     * @param id       节点ID
     * @param newName  新名称
     * @return 更新后的节点
     */
    public FileNode updateNodeName(String id, String newName) {
        Update update = new Update().set("name", newName);
        return mongoDBUtil.updateById(id, update, FileNode.class, COLLECTION_NAME);
    }

    /**
     * 更新节点孩子
     *
     * @param id       节点ID
     * @param children  新子节点
     * @return 更新后的节点
     */
    public FileNode updateNodeChildren(String id, List children) {
        Update update = new Update().set("children", children);
        return mongoDBUtil.updateById(id, update, FileNode.class, COLLECTION_NAME);
    }

    /**
     * 更新节点的父节点ID（移动节点）
     *
     * @param id       节点ID
     * @param newParentId 新的父节点ID
     * @return 更新后的节点
     */
    public FileNode updateNodeParent(String id, String newParentId) {
        Update update = new Update().set("parentId", newParentId);
        return mongoDBUtil.updateById(id, update, FileNode.class, COLLECTION_NAME);
    }

    /**
     * 删除节点
     *
     * @param id 节点ID
     */
    public void deleteNode(String id) {
        mongoDBUtil.deleteById(id, FileNode.class, COLLECTION_NAME);
    }

    /**
     * 递归删除节点及其子节点
     *
     * @param id 节点ID
     */
    public void deleteNodeRecursively(String id) {
        FileNode node = getNodeById(id);
        if (node != null) {
            // 如果是文件夹节点，递归删除子节点
            if ("folder".equals(node.getType()) && node.getChildren() != null) {
                for (String childId : node.getChildren()) {
                    deleteNodeRecursively(childId);
                }
            }
            // 删除当前节点
            deleteNode(id);
        }
    }

    /**
     * 查询完整的树状结构（从指定节点开始）
     *
     * @param nodeId 起始节点ID
     * @return 树状结构的根节点
     */
    public String getTree(String nodeId) {
        List<FileNode> treeNodes = getTreeNodes(nodeId);
        return JSON.toJSONString(treeNodes);
//        return JSON.toJSONStringWithDateFormat(treeNodes, "yyyy - MM - dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);

    }

    private List<FileNode> getTreeNodes(String nodeId) {
        List<FileNode> nodeArr = new ArrayList<FileNode>();
        FileNode nodeById = getNodeById(nodeId);
        nodeArr.add(nodeById);
        nodeById.getChildren().forEach(node -> {
            if (node == null) {
                return;
            }
            FileNode temp = getNodeById(node);
            if (temp.getChildren() == null || temp.getChildren().isEmpty()) {
                nodeArr.add(temp);
            }else {
                nodeArr.addAll(getTreeNodes(node));
            }
        });
        return nodeArr;
    }

    public void deleteFolder(String folderId) {
        List<FileNode> fileTree = getTreeNodes(folderId);
        fileTree.forEach(node -> {
            fileService.deleteFileById(node.getId());
            if (!"folder".equals(node.getType())) {
                try {
                    AliOssUtil.deleteFile(node.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

}