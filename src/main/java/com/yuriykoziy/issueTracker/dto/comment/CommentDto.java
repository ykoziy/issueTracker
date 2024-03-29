package com.yuriykoziy.issueTracker.dto.comment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private String content;
    private Long authorId;
    private Long issueId;
    private Long id;
    private LocalDateTime addedOn;
    private LocalDateTime updatedOn;
    private String username;
}
