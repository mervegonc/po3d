package com.project.po3d.entity;

import java.sql.Timestamp;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    private User user;

    private Timestamp expiresAt;
}

