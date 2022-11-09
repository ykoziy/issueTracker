package com.yuriykoziy.issueTracker.dto.issue;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.models.UserProfile;
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
    private LocalDateTime createdOn;
    private Long closerId;
    private LocalDateTime updatedOn;
    private LocalDateTime closedOn;
    private String resolution;
}
