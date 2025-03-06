package com.xiyun.saltedfishnetdish.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileShares {

  private String shareId;
  private String fileId;
  private long ownerId;
  private String shareType;
  private String passwordHash;
  private java.sql.Timestamp expiration;
  private long downloadCount;
  private java.sql.Timestamp createdAt;

}
