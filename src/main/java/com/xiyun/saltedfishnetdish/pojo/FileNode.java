package com.xiyun.saltedfishnetdish.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "filesTree")
public class FileNode {
    @Id
    private String id; // 节点ID
    private String name; // 节点名称
    private String type; // 节点类型：file 或 folder
    private String parentId; // 父节点ID
    private List<String> children; // 子节点ID列表
}
