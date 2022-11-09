package com.yuriykoziy.issueTracker.dto.comment;

import lombok.Data;

@Data
public class NewCommentDto {
    private String content;
    private Long userId;
    private Long issueId;
}
