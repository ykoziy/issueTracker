package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.dto.comment.CommentDto;
import com.yuriykoziy.issueTracker.dto.comment.NewCommentDto;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.models.Comment;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.CommentRepository;
import com.yuriykoziy.issueTracker.repositories.IssueRepository;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new IllegalStateException("no user found");
        }
        Optional<Issue> issueOptional = issueRepository.findById(newComment.getIssueId());
        if (!issueOptional.isPresent()) {
            throw new IllegalStateException("no issue found");
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
    public Long userDeleteComment(Long userId, Long commentId) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("no user found");
        }
        Optional<Comment> commentOptional = commentRepository.findByIdAndAuthorId(commentId, userId);
        if (commentOptional.isPresent()) {
            return commentRepository.removeById(commentId);
        } else {
            throw new IllegalStateException("no comment associated with the user found");
        }

    }
}
