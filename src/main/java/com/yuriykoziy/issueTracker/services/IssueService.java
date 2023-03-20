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
import com.yuriykoziy.issueTracker.util.CommonUtil;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "updatedOn", "createdOn");
        Pageable paging = PageRequest.of(page, size, sort);
        Page<Issue> issuePage = issueRepository.findAll(paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));
    }

    public Page<IssueDto> findAllCriteria(
            IssueStatus issueStatus,
            IssuePriority issuePriority,
            Long creatorId,
            int page,
            int size,
            String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "updatedOn", "createdOn");
        Pageable paging = PageRequest.of(page, size, sort);
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
        UserProfile closer;
        if (CommonUtil.isAdmin(auth)) {
            closer = (UserProfile) auth.getPrincipal();
        } else {
            Long userId = closeIssueDto.getUserId();
            closer = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(ErrorMessages.NO_USER_FOUND));
            if (!issue.getCreator().getId().equals(userId)) {
                throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
            }
        }
        issue.setResolution(closeIssueDto.getResolution());
        issue.setClosedOn(LocalDateTime.now());
        issue.setStatus(IssueStatus.CLOSED);
        issue.setCloser(closer);
    }

    public void updateIssue(Long userId, IssueDto issueDto) {
        UserProfile user = userProfileRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }

        Issue issue = issueRepository.findById(issueDto.getId())
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (CommonUtil.isAdmin(auth) || issue.getCreator().getId().equals(userId)) {
            modelMapper.map(issueDto, issue);
            issueRepository.save(issue);
        } else {
            throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
        }
    }

    @Transactional
    public void deleteIssue(Long userId, Long issueId) {
        UserProfile user = userProfileRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (CommonUtil.isAdmin(auth) || issue.getCreator().getId().equals(userId)) {
            issueRepository.removeById(issueId);
        } else {
            throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
        }
    }
}
