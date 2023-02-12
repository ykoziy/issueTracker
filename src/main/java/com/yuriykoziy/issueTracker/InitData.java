package com.yuriykoziy.issueTracker;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.enums.UserRole;
import com.yuriykoziy.issueTracker.models.Comment;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.CommentRepository;
import com.yuriykoziy.issueTracker.repositories.IssueRepository;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class InitData implements ApplicationRunner {

    private IssueRepository issueRepository;
    private CommentRepository commentRepository;
    private PasswordEncoder passwordEncoder;

    private UserProfileRepository userProfileRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String pwd = passwordEncoder.encode("password");
        UserProfile userA = new UserProfile("Bob", "Smith", "bob@example.com", "bsmith765", pwd, UserRole.USER);
        UserProfile userB = new UserProfile("Alice", "Doe", "alice.doe@example.com", "darmouse", pwd, UserRole.USER);
        UserProfile userC = new UserProfile("Thomas", "Anderson", "thomas.anderson@example.com", "Neo", pwd,
                UserRole.ADMIN);
        userProfileRepository.save(userA);
        userProfileRepository.save(userB);
        userProfileRepository.save(userC);

        Issue a = new Issue("An issue #1", "some low priority issue", userA);
        Issue c = new Issue("An issue with JPA", "there is an issue but it is low priority", userB);
        Issue b = new Issue("An issue #2", "some low priority issue", IssuePriority.HIGH, userA);
        Issue d = new Issue("Some resolved issue", "this issue is already resolved", IssuePriority.MEDIUM,
                IssueStatus.CLOSED, userB);
        d.setResolution("An easy fix, just fixed spelling");
        d.setCloser(userB);
        d.setClosedOn(LocalDateTime.now());
        issueRepository.save(a);
        issueRepository.save(b);
        issueRepository.save(c);
        issueRepository.save(d);

        Comment parent = new Comment(userA, "Any input guys?", a);
        Comment p1 = new Comment(userB, "Nice find, I did notice the same issue!", a);
        Comment p2 = new Comment(userC, "ok...", a);
        commentRepository.save(parent);
        commentRepository.save(p1);
        commentRepository.save(p2);
    }
}
