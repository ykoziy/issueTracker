package com.yuriykoziy.issueTracker.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.yuriykoziy.issueTracker.models.UserProfile;

@Transactional(readOnly = true)
public interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByUsername(String username);

    Page<UserProfile> findByEnabled(boolean enabled, Pageable pageable);
}
