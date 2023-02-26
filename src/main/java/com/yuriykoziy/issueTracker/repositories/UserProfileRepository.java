package com.yuriykoziy.issueTracker.repositories;

import com.yuriykoziy.issueTracker.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByUsername(String username);

    List<UserProfile> findByEnabled(boolean enabled);
}
