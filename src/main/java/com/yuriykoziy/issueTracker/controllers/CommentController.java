package com.yuriykoziy.issueTracker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuriykoziy.issueTracker.constants.ResponseConstants;
import com.yuriykoziy.issueTracker.dto.comment.CommentDto;
import com.yuriykoziy.issueTracker.dto.comment.NewCommentDto;
import com.yuriykoziy.issueTracker.services.CommentService;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping(path = "/comments")
    public void addNewComment(@RequestBody NewCommentDto newComment) {
        commentService.addComment(newComment);
    }

    @GetMapping(path = "/issues/{id}/comments")
    public Map<String, Object> getIssueComments(@PathVariable Long id, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<CommentDto> commentPage = commentService.getCommentsForIssue(id, page, size);
        List<CommentDto> comments = commentPage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.COMMENTS, comments);
        response.put(ResponseConstants.NUMBER, commentPage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, commentPage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, commentPage.getTotalPages());
        response.put(ResponseConstants.SIZE, commentPage.getSize());
        return response;
    }

    @DeleteMapping(path = "/comments/{id}")
    public void userDeleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

    @PutMapping(path = "/comments")
    public void userUpdateComment(@RequestBody CommentDto comment) {
        commentService.updateComment(comment);
    }
}
