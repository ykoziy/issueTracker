package com.yuriykoziy.issueTracker.controllers;

import com.yuriykoziy.issueTracker.dto.issue.CloseIssueDto;
import com.yuriykoziy.issueTracker.dto.issue.IssueDto;
import com.yuriykoziy.issueTracker.dto.issue.NewIssueDto;
import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import com.yuriykoziy.issueTracker.services.IssueService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "api/v1/issue")
@AllArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @GetMapping
    public List<IssueDto> getAll()
    {
        return issueService.findAll();
    }

    @GetMapping("/{id}")
    public IssueDto findIssueById(@PathVariable("id") Long id) {return issueService.findById(id);}

    @GetMapping(value = "/open", params = "id")
    public List<IssueDto> findIssuesOpenedById(@RequestParam Long id) {
        return issueService.findOpenedByUser(id);
    }

    @GetMapping(value = "/closed", params = "id")
    public List<IssueDto> findIssuesClosedById(@RequestParam Long id) {
        return issueService.findClosedByUser(id);
    }

    @GetMapping(params = "status")
    public List<IssueDto> filterByStatus(@RequestParam IssueStatus status) {
        return issueService.findByStatus(status);
    }

    @GetMapping(params = "priority")
    public List<IssueDto> filterByPriority(@RequestParam IssuePriority priority) {
        return issueService.findByPriority(priority);
    }

    @PostMapping
    public void createNewIssue(@RequestBody NewIssueDto newIssueDto) {
        issueService.addNewIssue(newIssueDto);
    }

    @PostMapping("/close")
    public void closeIssue(@RequestBody CloseIssueDto closeIssueDto) {
        issueService.closeIssue(closeIssueDto);
    }
}
