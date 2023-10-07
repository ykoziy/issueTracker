package com.yuriykoziy.issueTracker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yuriykoziy.issueTracker.models.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  VerificationToken findByToken(String token);
}
