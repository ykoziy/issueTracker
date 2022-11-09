package com.yuriykoziy.issueTracker.repositories;

import com.yuriykoziy.issueTracker.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAll();
    List<Comment> findAllByIssueId(Long id);
}
