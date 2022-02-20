package com.young.blogusbackend.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "token")
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private Bloger bloger;

    private Instant expiryDate;
}
