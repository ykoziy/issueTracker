package com.yuriykoziy.issueTracker.repositories;

import com.yuriykoziy.issueTracker.models.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
    List<Comment> findAll();

    Page<Comment> findAllByIssueId(Long id, Pageable pageable);

    Optional<Comment> findByIdAndAuthorId(Long id, Long authorId);

    Long removeById(Long id);
}
