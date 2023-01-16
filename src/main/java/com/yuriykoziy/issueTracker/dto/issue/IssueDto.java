package com.yuriykoziy.issueTracker.dto.issue;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssueDto {
    private Long id;
    private String title;
    private String description;
    private IssuePriority priority;
    private IssueStatus status;
    private Long creatorId;
    private String creatorUsername;
    private LocalDateTime createdOn;
    private Long closerId;
    private String closerUsername;
    private LocalDateTime updatedOn;
    private LocalDateTime closedOn;
    private String resolution;
}
