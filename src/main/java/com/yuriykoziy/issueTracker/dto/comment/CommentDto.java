package com.yuriykoziy.issueTracker.dto.comment;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private String content;
    private Long authorId;
    private Long issueId;
    private Long id;
    private LocalDateTime addedOn;
    private LocalDate updatedOn;
    private String username;
}
