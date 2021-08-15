package com.young.blogusbackend.modules.user;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id") @Builder
@NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String avatar = "https://res.cloudinary.com/dw6i0vp1r/image/upload/v1608643768/vvcldnbsfevfzpnzvtta.jpg";

    @Column(nullable = false)
    private String role = "user";

    @Column(nullable = false)
    private String type = "normal";

    @CreatedDate
    private LocalDateTime registeredAt;
}
