package com.yuriykoziy.issueTracker.models;


import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne()
    @JoinColumn(name = "issue_id")
    private Issue issue;

    public Comment(UserProfile author, String content, Issue issue) {
        this.author = author;
        this.content = content;
        this.issue = issue;
    }
}
