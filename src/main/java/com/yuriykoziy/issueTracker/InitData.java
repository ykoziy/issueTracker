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
                UserProfile userB = new UserProfile("Alice", "Doe", "alice.doe@example.com", "darmouse", pwd,
                                UserRole.USER);
                UserProfile userC = new UserProfile("Thomas", "Anderson", "thomas.anderson@example.com", "Neo", pwd,
                                UserRole.ADMIN);

                UserProfile userD = new UserProfile("Velma", "Stede", "vstede0@comcast.net", "vstede0", pwd,
                                UserRole.USER);
                UserProfile userE = new UserProfile("Roderich", "Ricarde", "rricarde1@fda.gov", "rricarde1", pwd,
                                UserRole.USER);
                UserProfile userF = new UserProfile("Teresina", "Adamowicz", "tadamowicz2@google.com.hk", "tadamowicz2",
                                pwd,
                                UserRole.USER);
                UserProfile userG = new UserProfile("Annie", "Bissatt", "abissatt3@cocolog-nifty.com", "abissatt3", pwd,
                                UserRole.USER);

                userA.setEnabled(true);
                userB.setEnabled(true);
                userC.setEnabled(true);
                userD.setEnabled(true);
                userE.setEnabled(true);
                userF.setEnabled(true);
                userG.setEnabled(true);
                userProfileRepository.save(userA);
                userProfileRepository.save(userB);
                userProfileRepository.save(userC);
                userProfileRepository.save(userD);
                userProfileRepository.save(userE);
                userProfileRepository.save(userF);
                userProfileRepository.save(userG);

                Issue a = new Issue("An issue #1", "some low priority issue", userA);
                Issue c = new Issue("An issue with JPA", "there is an issue but it is low priority", userB);
                Issue b = new Issue("An issue #2", "some low priority issue", IssuePriority.HIGH, userA);
                Issue d = new Issue("Some resolved issue", "this issue is already resolved", IssuePriority.MEDIUM,
                                IssueStatus.CLOSED, userB);
                Issue e = new Issue("Some wip issue", "this issue is in progress", IssuePriority.MEDIUM,
                                IssueStatus.WIP, userB);
                d.setResolution("An easy fix, just fixed spelling");
                d.setCloser(userB);
                d.setClosedOn(LocalDateTime.now());
                issueRepository.save(a);
                issueRepository.save(b);
                issueRepository.save(c);
                issueRepository.save(d);
                issueRepository.save(e);

                Comment parent = new Comment(userA, "Any input guys?", a);
                Comment p1 = new Comment(userB, "Nice find, I did notice the same issue!", a);
                Comment p2 = new Comment(userC, "ok...", a);
                commentRepository.save(parent);
                commentRepository.save(p1);
                commentRepository.save(p2);

                for (int i = 0; i <= 50; i++) {
                        commentRepository.save(new Comment(userB, "Nice find, I did notice the same issue!", b));
                }

                for (int i = 10; i <= 100; i++) {
                        issueRepository.save(new Issue("An issue #" + i, "some low priority issue", userA));
                }
        }
}
