package com.yuriykoziy.issueTracker.models;

import com.yuriykoziy.issueTracker.enums.IssuePriority;
import com.yuriykoziy.issueTracker.enums.IssueStatus;
import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @CreationTimestamp
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "closer_id")
    private UserProfile closer;
    @UpdateTimestamp
    private LocalDateTime updatedOn;
    private LocalDateTime closedOn;
    private String resolution;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

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
