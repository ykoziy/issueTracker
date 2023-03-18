package com.yuriykoziy.issueTracker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuriykoziy.issueTracker.constants.ResponseConstants;
import com.yuriykoziy.issueTracker.dto.issue.CloseIssueDto;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.dto.issue.NewIssueDto;
import com.yuriykoziy.issueTracker.models.IssueFilterCriteria;
import com.yuriykoziy.issueTracker.services.IssueService;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/issue")
@AllArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @GetMapping
    public Map<String, Object> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<IssueDto> issuePage = issueService.findAll(page, size);
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
    public IssueDto findIssueById(@PathVariable("id") Long id) {
        return issueService.findById(id);
    }

    @GetMapping(value = "/open", params = "id")
    public Map<String, Object> findIssuesOpenedById(@RequestParam Long id, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<IssueDto> issuePage = issueService.findOpenedByUser(id, page, size);
        List<IssueDto> issues = issuePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.ISSUES, issues);
        response.put(ResponseConstants.NUMBER, issuePage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, issuePage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, issuePage.getTotalPages());
        response.put(ResponseConstants.SIZE, issuePage.getSize());
        return response;
    }

    @GetMapping(value = "/closed", params = "id")
    public Map<String, Object> findIssuesClosedById(@RequestParam Long id, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<IssueDto> issuePage = issueService.findClosedByUser(id, page, size);
        List<IssueDto> issues = issuePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.ISSUES, issues);
        response.put(ResponseConstants.NUMBER, issuePage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, issuePage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, issuePage.getTotalPages());
        response.put(ResponseConstants.SIZE, issuePage.getSize());
        return response;
    }

    @GetMapping(value = "/filter")
    public Map<String, Object> filterBySpecs(
            IssueFilterCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<IssueDto> issuePage = issueService.findAllCriteria(criteria.getStatus(), criteria.getPriority(),
                criteria.getCreatorId(), page, size);

        List<IssueDto> issues = issuePage.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put(ResponseConstants.ISSUES, issues);
        response.put(ResponseConstants.NUMBER, issuePage.getNumber());
        response.put(ResponseConstants.TOTAL_ELEMENTS, issuePage.getTotalElements());
        response.put(ResponseConstants.TOTAL_PAGES, issuePage.getTotalPages());
        response.put(ResponseConstants.SIZE, issuePage.getSize());
        return response;
    }

    @PostMapping
    public void createNewIssue(@RequestBody NewIssueDto newIssueDto) {
        issueService.addNewIssue(newIssueDto);
    }

    @PostMapping("/close")
    public void closeIssue(@RequestBody CloseIssueDto closeIssueDto) {
        issueService.closeIssue(closeIssueDto);
    }

    @PostMapping("/edit")
    public void userUpdateIssue(@RequestParam Long userId, @RequestBody IssueDto issue) {
        issueService.updateIssue(userId, issue);
    }

    @DeleteMapping()
    public void deleteIssue(@RequestParam Long userId, @RequestParam Long issueId) {
        issueService.deleteIssue(userId, issueId);
    }
}
