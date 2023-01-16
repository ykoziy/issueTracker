package com.yuriykoziy.issueTracker.services;

import com.yuriykoziy.issueTracker.dto.issue.CloseIssueDto;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.dto.issue.NewIssueDto;
import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.models.Issue;
import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.repositories.IssueRepository;
import com.yuriykoziy.issueTracker.repositories.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final ModelMapper modelMapper;
    private final UserProfileRepository userProfileRepository;

    public List<IssueDto> findAll() {
        return issueRepository.findAll().stream().map(issue -> modelMapper.map(issue, IssueDto.class)).collect(Collectors.toList());
    }

    public List<IssueDto> findOpenedByUser(Long userId) {
        return issueRepository.findAllByCreatorId(userId).stream().map(issue -> modelMapper.map(issue, IssueDto.class)).collect(Collectors.toList());
    }

    public List<IssueDto> findClosedByUser(Long userId) {
        return issueRepository.findAllByCloserId(userId).stream().map(issue -> modelMapper.map(issue, IssueDto.class)).collect(Collectors.toList());
    }


    public List<IssueDto> findByPriority(IssuePriority issuePriority) {
        return issueRepository.findAllByPriority(issuePriority).stream().map(issue -> modelMapper.map(issue, IssueDto.class)).collect(Collectors.toList());
    }

    public List<IssueDto> findByStatus(IssueStatus issueStatus) {
        return issueRepository.findAllByStatus(issueStatus).stream().map(issue -> modelMapper.map(issue, IssueDto.class)).collect(Collectors.toList());
    }

    public IssueDto findById(Long issueId) {
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        if (!issueOptional.isPresent()) {
            throw new IllegalStateException("no issue found");
        }
        return modelMapper.map(issueOptional.get(), IssueDto.class);
    }

    @Transactional
    public void addNewIssue(NewIssueDto newIssueDto) {
        Optional<UserProfile> userOptional  = userProfileRepository.findById(newIssueDto.getUserId());
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("no user found");
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
            throw new IllegalStateException("no issue found");
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
            throw new IllegalStateException("issue does not belong to a user");
        }
        Optional<UserProfile> userOptional  = userProfileRepository.findById(closeIssueDto.getUserId());
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("no user found");
        }
        issue.setResolution(closeIssueDto.getResolution());
        issue.setClosedOn(LocalDateTime.now());
        issue.setUpdatedOn(LocalDateTime.now());
        issue.setStatus(IssueStatus.CLOSED);
        issue.setCloser(userOptional.get());
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
            throw new IllegalStateException("no user found");
        }

        Optional<Issue> issueOptional = issueRepository.findByIdAndCreatorId(issueId, userId);
        if (issueOptional.isPresent()) {
            return issueRepository.removeById(issueId);
        } else {
            throw new IllegalStateException("no issue associated with the user found");
        }

    }
}
