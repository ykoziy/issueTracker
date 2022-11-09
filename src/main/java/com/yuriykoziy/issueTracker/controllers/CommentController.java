package com.yuriykoziy.issueTracker.controllers;

import com.yuriykoziy.issueTracker.dto.comment.CommentDto;
import com.yuriykoziy.issueTracker.dto.comment.NewCommentDto;
import com.yuriykoziy.issueTracker.models.Comment;
import com.yuriykoziy.issueTracker.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void addNewComment(@RequestBody NewCommentDto newComment) {
        commentService.addComment(newComment);
    }

    @GetMapping(params = "issueId")
    public List<CommentDto> getIssueComments(@RequestParam Long issueId) {
        return commentService.getCommentsForIssue(issueId);
    }
}
