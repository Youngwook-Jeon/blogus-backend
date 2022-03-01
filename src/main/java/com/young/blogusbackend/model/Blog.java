package com.young.blogusbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "blog")
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob @Basic(fetch = FetchType.EAGER)
    private String content;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Lob @Basic(fetch = FetchType.EAGER)
    private String thumbnail;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bloger bloger;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}
