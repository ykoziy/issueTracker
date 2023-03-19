package com.yuriykoziy.issueTracker.models;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterCriteria {
  @Nullable
  private Boolean enabled;
  @Nullable
  private Boolean locked;
}