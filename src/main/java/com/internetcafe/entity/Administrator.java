package com.internetcafe.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administrators")
@Getter
@Setter
public class Administrator extends User {
    @Column(nullable=false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean isHr = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "administrator")
    private List<Log> logs = new ArrayList<>();
}
