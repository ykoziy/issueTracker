package com.yuriykoziy.issueTracker.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;

  @OneToOne(targetEntity = UserProfile.class, fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "user_id")
  private UserProfile user;

  private LocalDateTime expiryDate;

  public VerificationToken(String token, UserProfile userProfile) {
    this.token = token;
    this.user = userProfile;
    // Set an expiration time, e.g., 24 hours from the current time
    this.expiryDate = LocalDateTime.now().plusDays(1);
  }
}
