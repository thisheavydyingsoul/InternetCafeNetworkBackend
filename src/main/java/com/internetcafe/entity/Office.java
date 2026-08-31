package com.internetcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offices")
@Getter
@Setter
public class Office extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @OneToMany(mappedBy = "office")
    private List<Administrator> administrators = new ArrayList<>();

    @OneToMany(mappedBy = "office")
    private List<Device> devices = new ArrayList<>();
}
