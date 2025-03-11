package com.xiyun.saltedfishnetdish.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

  private String fileName;//文件名
  @JsonIgnore
  private String fileUuid;//文件UUID
  private String fileFormat;//文件格式
  private long fileSize;//文件大小（字节）
  private long downloadCount;//文件下载次数
  @JsonIgnore
  private String fileUrl;//文件URL
  private String viewPermission;//查看权限（PUBLIC：公开，PRIVATE：私有）
  private long creatorId;//文件创建者ID
  private String creatorNickname;//文件创建者昵称
  private java.sql.Timestamp createdAt;//文件创建时间
  private java.sql.Timestamp updatedAt;//文件更新时间

}
