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

    //change issue status to CLOSED, resolution message must be added (user)
    @Transactional
    public void closeIssue(CloseIssueDto closeIssueDto) {
        Optional<Issue> issueOptional = issueRepository.findById(closeIssueDto.getIssueId());
        if (!issueOptional.isPresent()) {
            throw new IllegalStateException("no issue found");
        }
        Issue issue = issueOptional.get();
        if (issue.getCreator().getId() != closeIssueDto.getUserId()) {
            throw new IllegalStateException("issue does not belong to a user");
        }
        Optional<UserProfile> userOptional  = userProfileRepository.findById(closeIssueDto.getUserId());
        if (!userOptional.isPresent()) {
            throw new IllegalStateException("no user found");
        }
        issue.setResolution(closeIssueDto.getResolution());
        issue.setClosedOn(LocalDateTime.now());
        issue.setUpdatedOn(LocalDateTime.now());
        issue.setCloser(userOptional.get());
    }

    //edit issue, admin can edit globally

    // edit issue, a user (cant close) - WIP RIGHT NOW
}
