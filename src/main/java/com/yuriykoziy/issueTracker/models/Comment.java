package com.yuriykoziy.issueTracker.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserProfile author;

    private String content;
    private LocalDateTime addedOn = LocalDateTime.now();
    private LocalDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    public Comment(UserProfile author, String content, Issue issue) {
        this.author = author;
        this.content = content;
        this.issue = issue;
    }
}
