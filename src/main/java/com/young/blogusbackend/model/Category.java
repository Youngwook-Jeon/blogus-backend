package com.young.blogusbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(
        name = "Category.withBlogsAndBloger",
        attributeNodes = {
                @NamedAttributeNode(value = "blogs", subgraph = "bloger")
        },
        subgraphs = @NamedSubgraph(name = "bloger", attributeNodes = @NamedAttributeNode("bloger"))
)
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

    @OneToMany(mappedBy = "category")
    @OrderBy("createdAt DESC")
    private List<Blog> blogs = new ArrayList<>();
}
