package com.young.blogusbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "bloger")
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Bloger {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String refreshToken;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean enabled;

    @PrePersist
    public void prePersist() {
        this.avatar = this.avatar == null ?
                "https://res.cloudinary.com/dw6i0vp1r/image/upload/v1608643768/vvcldnbsfevfzpnzvtta.jpg" :
                this.avatar;
    }
}
