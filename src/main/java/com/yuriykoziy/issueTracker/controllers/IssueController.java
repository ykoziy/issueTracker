package com.yuriykoziy.issueTracker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuriykoziy.issueTracker.constants.ResponseConstants;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.dto.issue.NewIssueDto;
import com.yuriykoziy.issueTracker.models.IssueFilterCriteria;
import com.yuriykoziy.issueTracker.services.IssueService;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/issues")
@AllArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @GetMapping()
    public Map<String, Object> getAllByCriteria(
            IssueFilterCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<IssueDto> issuePage = issueService.findAllCriteria(criteria.getStatus(), criteria.getPriority(),
                criteria.getCreatorId(), page, size, criteria.getOrder());

        List<IssueDto> issues = issuePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.ISSUES, issues);
        response.put(ResponseConstants.NUMBER, issuePage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, issuePage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, issuePage.getTotalPages());
        response.put(ResponseConstants.SIZE, issuePage.getSize());
        return response;
    }

    @GetMapping("/{id}")
    public IssueDto findIssueById(@PathVariable Long id) {
        return issueService.findById(id);
    }

    @PostMapping
    public void createNewIssue(@RequestBody NewIssueDto newIssueDto) {
        issueService.addNewIssue(newIssueDto);
    }

    @PutMapping("/{id}/close")
    public void closeIssue(@PathVariable Long id, @RequestBody String resolutionMessage) {
        issueService.closeIssue(id, resolutionMessage);
    }

    @PutMapping()
    public void userUpdateIssue(@RequestBody IssueDto issue) {
        issueService.updateIssue(issue);
    }

    @DeleteMapping("/{id}")
    public void deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
    }
}
