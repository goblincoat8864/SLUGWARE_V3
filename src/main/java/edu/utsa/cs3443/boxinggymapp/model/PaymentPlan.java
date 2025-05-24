package edu.utsa.cs3443.boxinggymapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment-plans")
@Getter
@Setter
public class PaymentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "frequency-in-days", nullable = false)
    private Integer frequencyInDays;

    @ManyToOne
    @JoinColumn(name = "gym-owner-id", nullable = false)
    private GymOwner gymOwner;

    @OneToMany(mappedBy = "paymentPlan")
    private Set<GymMember> gymMembers = new HashSet<>();
}

