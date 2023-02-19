package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.comment.CommentDto;
import com.yuriykoziy.issueTracker.dto.comment.NewCommentDto;
import com.yuriykoziy.issueTracker.exceptions.UserNotFoundException;
import com.yuriykoziy.issueTracker.models.Comment;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.CommentRepository;
import com.yuriykoziy.issueTracker.repositories.IssueRepository;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    private final UserProfileRepository userProfileRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void addComment(NewCommentDto newComment) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(newComment.getUserId());
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        Optional<Issue> issueOptional = issueRepository.findById(newComment.getIssueId());
        if (!issueOptional.isPresent()) {
            throw new IllegalStateException(ErrorMessages.ISSUE_NOT_FOUND);
        }
        Comment comment = new Comment(userOptional.get(), newComment.getContent(), issueOptional.get());
        commentRepository.save(comment);
    }

    public List<CommentDto> getCommentsForIssue(Long issueId) {
        List<Comment> comments = commentRepository.findAllByIssueId(issueId);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto dto = new CommentDto();
            modelMapper.map(comment, dto);
            dto.setUsername(comment.getAuthor().getUsername());
            commentsDto.add(dto);
        }
        return commentsDto;
    }

    @Transactional
    public Long deleteComment(Long userId, Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            Optional<Comment> commentOptional = commentRepository.findById(commentId);
            if (commentOptional.isPresent()) {
                return commentRepository.removeById(commentId);
            }
        }

        Optional<UserProfile> userOptional = userProfileRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        Optional<Comment> commentOptional = commentRepository.findByIdAndAuthorId(commentId, userId);
        if (commentOptional.isPresent()) {
            return commentRepository.removeById(commentId);
        } else {
            throw new IllegalStateException(ErrorMessages.NO_USER_COMMENT_FOUND);
        }

    }

    public boolean updateComment(Long userId, CommentDto comment) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        Optional<Comment> commentOptional = commentRepository.findById(comment.getId());
        if (!commentOptional.isPresent()) {
            throw new IllegalStateException(ErrorMessages.NO_COMMENT_FOUND);
        }
        Comment editComment = commentOptional.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            modelMapper.map(comment, editComment);
            editComment.setUpdatedOn(LocalDateTime.now());
            commentRepository.save(editComment);
            return true;
        }
        if (!editComment.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException(ErrorMessages.NO_USER_COMMENT_FOUND);
        }
        modelMapper.map(comment, editComment);
        editComment.setUpdatedOn(LocalDateTime.now());
        commentRepository.save(editComment);
        return true;
    }
}
