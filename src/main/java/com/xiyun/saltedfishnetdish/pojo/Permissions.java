package com.xiyun.saltedfishnetdish.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permissions {

  private long permId;
  private String permName;
  private String description;

}
