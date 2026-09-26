package com.internetcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column
    private String passwordHash;

    @Column(name="google_sub", unique = true)
    private String googleSub;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified = false;

    private LocalDateTime emailVerifiedAt;
}
