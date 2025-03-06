package com.xiyun.saltedfishnetdish.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friendships {

  private long userA;
  private long userB;
  private String status;
  private java.sql.Timestamp createdAt;

}
