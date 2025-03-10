package com.xiyun.saltedfishnetdish.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

  private String fileName;
  private String fileUuid;
  private String fileFormat;
  private long fileSize;
  private long downloadCount;
  private String fileUrl;
  private String viewPermission;
  private long creatorId;
  private String creatorNickname;
  private java.sql.Timestamp createdAt;
  private java.sql.Timestamp updatedAt;

}
