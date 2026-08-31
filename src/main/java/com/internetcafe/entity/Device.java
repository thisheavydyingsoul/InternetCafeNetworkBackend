package com.internetcafe.entity;

import com.internetcafe.enums.DeviceCondition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "devices")
@Getter
@Setter
public class Device extends BaseEntity {

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeviceCondition condition;

    @Column(name = "day_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dayRate;

    @Column(name = "night_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal nightRate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToMany
    @JoinTable(
            name = "device_games",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private Set<Game> games = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private List<Rent> rents = new ArrayList<>();
}
