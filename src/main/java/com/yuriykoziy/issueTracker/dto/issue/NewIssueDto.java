package com.yuriykoziy.issueTracker.dto.issue;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import lombok.Data;

@Data
public class NewIssueDto {
    private String title;
    private String description;
    private IssuePriority priority;
    private Long userId;
}
