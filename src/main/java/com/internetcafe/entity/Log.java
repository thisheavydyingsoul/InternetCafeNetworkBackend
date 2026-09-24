package com.internetcafe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logs")
@Getter
@Setter
public class Log extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id")
    private Administrator administrator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(length = 50)
    private String action;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
}