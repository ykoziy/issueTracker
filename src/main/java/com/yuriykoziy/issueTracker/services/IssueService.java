package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.constants.ErrorMessages;
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
    public void closeIssue(Long issueId, String resolutionMessage) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserProfile closer = (UserProfile) auth.getPrincipal();
        String username = auth.getName();

        if (!CommonUtil.isAdmin(auth) && !issue.getCreator().getUsername().equals(username)) {
            throw new IssueException("User is not authorized to close this issue");
        }

        issue.setResolution(resolutionMessage);
        issue.setClosedOn(LocalDateTime.now());
        issue.setStatus(IssueStatus.CLOSED);
        issue.setCloser(closer);
    }

    public void updateIssue(IssueDto issueDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<UserProfile> userOptional = userProfileRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new IssueException(ErrorMessages.NO_USER_FOUND);
        }

        Issue issue = issueRepository.findById(issueDto.getId())
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        if (!(CommonUtil.isAdmin(auth) || issue.getCreator().getUsername().equals(username))) {
            throw new IssueException("User is not authorized to update this issue");
        }

        modelMapper.map(issueDto, issue);
        issueRepository.save(issue);
    }

    @Transactional
    public void deleteIssue(Long issueId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<UserProfile> userOptional = userProfileRepository.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new IssueException(ErrorMessages.NO_USER_FOUND);
        }

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueException(ErrorMessages.ISSUE_NOT_FOUND));

        if (!(CommonUtil.isAdmin(auth) || issue.getCreator().getUsername().equals(username))) {
            throw new IssueException("User is not authorized to delete this issue");
        }

        issueRepository.removeById(issueId);
    }
}
