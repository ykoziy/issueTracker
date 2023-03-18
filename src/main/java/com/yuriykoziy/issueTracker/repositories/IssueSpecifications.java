package com.yuriykoziy.issueTracker.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.models.UserProfile;

public interface IssueSpecifications {
  // define filtering criteria for the query
  static Specification<Issue> filterByCriteria(IssueStatus issueStatus, IssuePriority issuePriority, Long creatorId) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (issueStatus != null) {
        predicates.add(cb.equal(root.get("status"), issueStatus));
      }
      if (issuePriority != null) {
        predicates.add(cb.equal(root.get("priority"), issuePriority));
      }
      if (creatorId != null) {
        Join<Issue, UserProfile> userProfileJoin = root.join("creator");
        predicates.add(cb.equal(userProfileJoin.get("id"), creatorId));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
