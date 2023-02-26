package com.yuriykoziy.issueTracker.controllers;

import com.yuriykoziy.issueTracker.dto.comment.CommentDto;
import com.yuriykoziy.issueTracker.dto.comment.NewCommentDto;
import com.yuriykoziy.issueTracker.services.CommentService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    public Map<String, Object> getIssueComments(@RequestParam Long issueId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<CommentDto> commentPage = commentService.getCommentsForIssue(issueId, page, size);
        List<CommentDto> comments = commentPage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("comments", comments);
        response.put("number", commentPage.getNumber());
        response.put("totalElements", commentPage.getTotalElements());
        response.put("totalPages", commentPage.getTotalPages());
        response.put("size", commentPage.getSize());
        return response;
    }

    @DeleteMapping()
    public ResponseEntity<Long> userDeleteComment(@RequestParam Long userId, @RequestParam Long commentId) {
        Long result = commentService.deleteComment(userId, commentId);
        if (result != 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/edit")
    public boolean userUpdateComment(@RequestParam Long userId, @RequestBody CommentDto comment) {
        return commentService.updateComment(userId, comment);
    }
}
