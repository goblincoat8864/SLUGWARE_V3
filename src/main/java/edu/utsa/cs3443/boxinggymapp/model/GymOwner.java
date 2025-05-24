package edu.utsa.cs3443.boxinggymapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "gym-owners")
@Getter
@Setter
public class GymOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "gym-address")
    private String gymAddress;

    @Column(name = "phone-number")
    private String phoneNumber;

    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "paid-until")
    private LocalDateTime paidUntil;

    @Column(name = "stripe-customer-id")
    private String stripeCustomerId;

    @Column(name = "stripe-account-id")
    private String stripeAccountId;

    @Column(name = "payment-method-id")
    private String paymentMethodId;

    @Column(name = "subscription-id")
    private String subscriptionId;

    @OneToMany(mappedBy = "gymOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentPlan> paymentPlans = new HashSet<>();

    @OneToMany(mappedBy = "gymOwner")
    private Set<GymMember> gymMembers = new HashSet<>();

    @OneToMany(mappedBy = "gymOwner")
    private Set<GymRecord> gymRecords = new HashSet<>();
}
