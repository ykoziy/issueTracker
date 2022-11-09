package com.yuriykoziy.issueTracker.models;
import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private IssuePriority priority = IssuePriority.LOW;
    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.OPEN;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserProfile creator;
    private LocalDateTime createdOn = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "closer_id")
    private UserProfile closer;
    private LocalDateTime updatedOn;
    private LocalDateTime closedOn;
    private String resolution;



    public Issue(String title, String description, IssuePriority priority, UserProfile creator) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.creator = creator;
    }

    public Issue(String title, String description, IssueStatus status, UserProfile creator) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.creator = creator;
    }

    public Issue(String title, String description, UserProfile creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
    }

    public Issue(String title, String description, IssuePriority priority, IssueStatus status, UserProfile creator) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.creator = creator;
    }
}
