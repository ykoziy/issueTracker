package com.yuriykoziy.issueTracker.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.yuriykoziy.issueTracker.models.UserProfile;

public interface UserSpecifications {
  static Specification<UserProfile> filterByCriteria(Boolean enabled, Boolean locked) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (enabled != null) {
        predicates.add(cb.equal(root.get("enabled"), enabled));
      }
      if (locked != null) {
        predicates.add(cb.equal(root.get("locked"), locked));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
