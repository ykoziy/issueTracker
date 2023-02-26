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

    public Page<IssueDto> findByPriority(IssuePriority issuePriority, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findAllByPriority(issuePriority, paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));
    }

    public Page<IssueDto> findByStatus(IssueStatus issueStatus, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findAllByStatus(issueStatus, paging);
        return issuePage.map(issue -> modelMapper.map(issue, IssueDto.class));
    }

    public Page<IssueDto> findByStatusAndPriority(IssueStatus issueStatus, IssuePriority issuePriority, int page,
            int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Issue> issuePage = issueRepository.findByStatusAndPriority(issueStatus, issuePriority, paging);
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
        Optional<Issue> issueOptional = issueRepository.findById(closeIssueDto.getIssueId());
        if (!issueOptional.isPresent()) {
            throw new IssueException(ErrorMessages.ISSUE_NOT_FOUND);
        }

        Issue issue = issueOptional.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            UserProfile adminProfile = (UserProfile) auth.getPrincipal();
            issue.setResolution(closeIssueDto.getResolution());
            issue.setClosedOn(LocalDateTime.now());
            issue.setUpdatedOn(LocalDateTime.now());
            issue.setStatus(IssueStatus.CLOSED);
            issue.setCloser(adminProfile);
            return;
        }

        if (!issue.getCreator().getId().equals(closeIssueDto.getUserId())) {
            throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
        }
        Optional<UserProfile> userOptional = userProfileRepository.findById(closeIssueDto.getUserId());
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        issue.setResolution(closeIssueDto.getResolution());
        issue.setClosedOn(LocalDateTime.now());
        issue.setUpdatedOn(LocalDateTime.now());
        issue.setStatus(IssueStatus.CLOSED);
        issue.setCloser(userOptional.get());
    }

    public boolean updateIssue(Long userId, IssueDto issue) {
        Optional<UserProfile> userOptional = userProfileRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }
        Optional<Issue> issueOptional = issueRepository.findById(issue.getId());
        if (!issueOptional.isPresent()) {
            throw new IssueException(ErrorMessages.ISSUE_NOT_FOUND);
        }
        Issue editIssue = issueOptional.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            modelMapper.map(issue, editIssue);
            editIssue.setUpdatedOn(LocalDateTime.now());
            issueRepository.save(editIssue);
            return true;
        }
        if (!editIssue.getCreator().getId().equals(userId)) {
            throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
        }
        modelMapper.map(issue, editIssue);
        editIssue.setUpdatedOn(LocalDateTime.now());
        issueRepository.save(editIssue);
        return true;
    }

    @Transactional
    public Long deleteIssue(Long userId, Long issueId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            Optional<Issue> issueOptional = issueRepository.findById(issueId);
            if (issueOptional.isPresent()) {
                return issueRepository.removeById(issueId);
            }
        }

        Optional<UserProfile> userOptional = userProfileRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException(ErrorMessages.NO_USER_FOUND);
        }

        Optional<Issue> issueOptional = issueRepository.findByIdAndCreatorId(issueId, userId);
        if (issueOptional.isPresent()) {
            return issueRepository.removeById(issueId);
        } else {
            throw new IssueException(ErrorMessages.NO_USER_ISSUE_FOUND);
        }

    }
}
