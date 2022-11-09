package com.yuriykoziy.issueTracker.dto.issue;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import lombok.Data;

@Data
public class UpdateIssueDto {
    private String title;
    private String description;
    private IssuePriority priority;
    private IssueStatus status;
    private Long userId;
    private String resolution;
}
