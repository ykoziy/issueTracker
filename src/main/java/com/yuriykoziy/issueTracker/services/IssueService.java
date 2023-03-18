package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
import com.yuriykoziy.issueTracker.dto.issue.CloseIssueDto;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.dto.issue.NewIssueDto;
import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.exceptions.IssueException;
import com.yuriykoziy.issueTracker.exceptions.UserNotFoundException;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.IssueRepository;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ModelMapper modelMapper;
    private final UserProfileRepository userProfileRepository;

    public Page<IssueDto> findAll(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findAll(paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));
    }

    public Page<IssueDto> findAllCriteria(
            IssueStatus issueStatus,
            IssuePriority issuePriority,
            Long creatorId,
            int page,
            int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findByCriteria(issueStatus, issuePriority, creatorId, paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));

    }

    public Page<IssueDto> findOpenedByUser(Long userId, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findAllByCreatorId(userId, paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));
    }

    public Page<IssueDto> findClosedByUser(Long userId, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findAllByCloserId(userId, paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));
    }

    public IssueDto findById(Long issueId) {
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        if (!issueOptional.isPresent()) {
            throw new IssueException(ErrorMessages.ISSUE_NOT_FOUND);
        }
        return modelMapper.map(issueOptional.get(), IssueDto.class);
    }

    @Transactional
    public void addNewIssue(NewIssueDto newIssueDto) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(newIssueDto.getUserId());
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        Issue newIssue = new Issue();
        modelMapper.map(newIssueDto, newIssue);
        newIssue.setId(null);
        newIssue.setCreator(userOptional.get());
        issueRepository.save(newIssue);
    }

    @Transactional
    public void closeIssue(CloseIssueDto closeIssueDto) {
        Issue issue = issueRepository.findById(closeIssueDto.getIssueId())
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            UserProfile adminProfile = (UserProfile) auth.getPrincipal();
            issue.setResolution(closeIssueDto.getResolution());
            issue.setClosedOn(LocalDateTime.now());
            issue.setUpdatedOn(LocalDateTime.now());
            issue.setStatus(IssueStatus.CLOSED);
            issue.setCloser(adminProfile);
        } else {
            Long userId = closeIssueDto.getUserId();
            if (!issue.getCreator().getId().equals(userId)) {
                throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
            }
            UserProfile user = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(ErrorMessages.NO_USER_FOUND));
            issue.setResolution(closeIssueDto.getResolution());
            issue.setClosedOn(LocalDateTime.now());
            issue.setUpdatedOn(LocalDateTime.now());
            issue.setStatus(IssueStatus.CLOSED);
            issue.setCloser(user);
        }
    }

    public void updateIssue(Long userId, IssueDto issueDto) {
        UserProfile user = userProfileRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }

        Issue issue = issueRepository.findById(issueDto.getId())
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            modelMapper.map(issueDto, issue);
            issue.setUpdatedOn(LocalDateTime.now());
            issueRepository.save(issue);
        } else {
            if (!issue.getCreator().getId().equals(userId)) {
                throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
            }
            modelMapper.map(issueDto, issue);
            issue.setUpdatedOn(LocalDateTime.now());
            issueRepository.save(issue);
        }
    }

    @Transactional
    public Long deleteIssue(Long userId, Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return issueRepository.removeById(issueId);
        } else {
            UserProfile user = userProfileRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
            }

            if (!issue.getCreator().getId().equals(userId)) {
                throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
            }

            return issueRepository.removeById(issueId);
        }
    }
}
