package com.yuriykoziy.issueTracker.models;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

import java.util.Optional;

import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueFilterCriteria {
  @Nullable
  @Setter(AccessLevel.NONE)
  private IssueStatus status;
  @Nullable
  @Setter(AccessLevel.NONE)
  private IssuePriority priority;
  @Nullable
  private Long creatorId;

  public void setStatus(String status) {
    this.status = Optional.ofNullable(status)
        .map(String::toUpperCase)
        .map(IssueStatus::valueOf)
        .orElse(null);
  }

  public void setPriority(String priority) {
    this.priority = Optional.ofNullable(priority)
        .map(String::toUpperCase)
        .map(IssuePriority::valueOf)
        .orElse(null);
  }
}
