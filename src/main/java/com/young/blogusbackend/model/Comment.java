package com.young.blogusbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "comment")
@Getter @Setter
@EqualsAndHashCode(of = "id") @Builder
@AllArgsConstructor @NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long blogId;

    @Column(nullable = false)
    private Long blogUserId;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bloger bloger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b_id", referencedColumnName = "id")
    private Blog blog;

    @PrePersist
    public void prePersist() {
        this.isDeleted = this.isDeleted != null && this.isDeleted;
    }
}
