package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.comment.CommentDto;
import com.yuriykoziy.issueTracker.dto.comment.NewCommentDto;
import com.yuriykoziy.issueTracker.exceptions.CommentException;
import com.yuriykoziy.issueTracker.exceptions.UserNotFoundException;
import com.yuriykoziy.issueTracker.models.Comment;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.util.CommonUtil;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.CommentRepository;
import com.yuriykoziy.issueTracker.repositories.IssueRepository;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
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
        UserProfile user = userProfileRepository.findById(newComment.getUserId())
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.NO_USER_FOUND));
        Issue issue = issueRepository.findById(newComment.getIssueId())
                .orElseThrow(() -> new CommentException(ErrorMessages.ISSUE_NOT_FOUND));

        Comment comment = new Comment(user, newComment.getContent(), issue);
        commentRepository.save(comment);
    }

    public Page<CommentDto> getCommentsForIssue(Long issueId, int page, int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findAllByIssueId(issueId, paging);
        List<CommentDto> commentsDto = new ArrayList<>();

        for (Comment comment : commentPage.getContent()) {
            CommentDto dto = new CommentDto();
            modelMapper.map(comment, dto);
            dto.setUsername(comment.getAuthor().getUsername());
            commentsDto.add(dto);
        }

        return new PageImpl<>(commentsDto, paging,
                commentPage.getTotalElements());
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        UserProfile user = userProfileRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(ErrorMessages.NO_COMMENT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (CommonUtil.isAdmin(auth) || comment.getAuthor().getId().equals(userId)) {
            commentRepository.removeById(commentId);
        } else {
            throw new CommentException(ErrorMessages.NO_USER_COMMENT_FOUND);
        }
    }

    public void updateComment(Long userId, CommentDto commentDto) {
        UserProfile user = userProfileRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }

        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new CommentException(ErrorMessages.NO_COMMENT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (CommonUtil.isAdmin(auth) || comment.getAuthor().getId().equals(userId)) {
            modelMapper.map(commentDto, comment);
            comment.setUpdatedOn(LocalDateTime.now());
            commentRepository.save(comment);
        } else {
            throw new CommentException(ErrorMessages.NO_USER_COMMENT_FOUND);
        }
    }
}
