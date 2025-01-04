package com.authms.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
@Entity
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    // Getters and Setters
}