package com.yuriykoziy.issueTracker.repositories;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAll();
    List<Issue> findAllByPriority(IssuePriority issuePriority);
    List<Issue> findAllByStatus(IssueStatus issueStatus);
    List<Issue> findAllByCloserId(Long id);
    List<Issue> findAllByCreatorId(Long id);
}
