package com.internetcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client extends User {

    private BigDecimal balance = 0.0;

    @OneToMany(mappedBy = "client")
    private List<Rent> rents = new ArrayList<>();
}