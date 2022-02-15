package com.young.blogusbackend.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "\"user\"")
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ColumnDefault(value = "https://res.cloudinary.com/dw6i0vp1r/image/upload/v1608643768/vvcldnbsfevfzpnzvtta.jpg")
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean enabled;
}
