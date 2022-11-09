package com.yuriykoziy.issueTracker.dto.issue;
import lombok.Data;

@Data
public class CloseIssueDto {
    private String resolution;
    private Long userId;
    private Long issueId;
}
