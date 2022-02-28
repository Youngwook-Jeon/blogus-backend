package com.young.blogusbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "category")
@Getter @Setter
@EqualsAndHashCode(of = "id") @Builder
@AllArgsConstructor @NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
