package com.yuriykoziy.issueTracker.repositories;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.models.Issue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {

    Page<Issue> findAllByPriority(IssuePriority issuePriority, Pageable pageable);

    Page<Issue> findAllByStatus(IssueStatus issueStatus, Pageable pageable);

    Page<Issue> findByStatusAndPriority(IssueStatus issueStatus, IssuePriority issuePriority, Pageable pageable);

    Page<Issue> findAllByCloserId(Long id, Pageable pageable);

    Page<Issue> findAllByCreatorId(Long id, Pageable pageable);

    Optional<Issue> findByIdAndCreatorId(Long id, Long authorId);

    Long removeById(Long id);
}
