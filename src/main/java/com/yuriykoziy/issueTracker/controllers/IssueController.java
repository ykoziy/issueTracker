package com.yuriykoziy.issueTracker.controllers;

import com.yuriykoziy.issueTracker.dto.issue.CloseIssueDto;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.dto.issue.NewIssueDto;
import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.services.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/issue")
@AllArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @GetMapping
    public List<IssueDto> getAll() {
        return issueService.findAll();
    }

    @GetMapping("/{id}")
    public IssueDto findIssueById(@PathVariable("id") Long id) {
        return issueService.findById(id);
    }

    @GetMapping(value = "/open", params = "id")
    public List<IssueDto> findIssuesOpenedById(@RequestParam Long id) {
        return issueService.findOpenedByUser(id);
    }

    @GetMapping(value = "/closed", params = "id")
    public List<IssueDto> findIssuesClosedById(@RequestParam Long id) {
        return issueService.findClosedByUser(id);
    }

    @GetMapping(params = "status")
    public List<IssueDto> filterByStatus(@RequestParam String status) {
        return issueService.findByStatus(IssueStatus.valueOf(status.toUpperCase()));
    }

    @GetMapping(params = "priority")
    public List<IssueDto> filterByPriority(@RequestParam String priority) {
        return issueService.findByPriority(IssuePriority.valueOf(priority.toUpperCase()));
    }

    @GetMapping(value = "/filter")
    public List<IssueDto> filterByStatusAndPriority(@RequestParam String status, @RequestParam String priority) {
        return issueService.findByStatusAndPriority(IssueStatus.valueOf(status.toUpperCase()),
                IssuePriority.valueOf(priority.toUpperCase()));
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
    public boolean userUpdateIssue(@RequestParam Long userId, @RequestBody IssueDto issue) {
        return issueService.updateIssue(userId, issue);
    }

    @DeleteMapping()
    public ResponseEntity<Long> deleteIssue(@RequestParam Long userId, @RequestParam Long issueId) {
        Long result = issueService.deleteIssue(userId, issueId);
        if (result != 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
