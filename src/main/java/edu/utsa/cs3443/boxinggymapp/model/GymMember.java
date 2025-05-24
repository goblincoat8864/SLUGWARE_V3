package edu.utsa.cs3443.boxinggymapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "gym-members")
@Getter
@Setter
public class GymMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, length = 4)
    private String pin;

    @Column(name = "date-of-birth")
    private LocalDate dateOfBirth;

    @Column(name = "phone-number")
    private String phoneNumber;

    @Column(name = "emergency-phone-number")
    private String emergencyPhoneNumber;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "signup-date")
    private LocalDate signUpDate;

    @Column(name = "next-payment-date")
    private LocalDate nextPaymentDate;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "picture")
    private byte[] picture;

    @Column(name = "stripe-customer-id")
    private String stripeCustomerId;

    @OneToMany(mappedBy = "gymMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GymMemberFile> pdfs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "gym-owner-id", nullable = false)
    private GymOwner gymOwner;

    @OneToMany(mappedBy = "gymMember")
    private Set<GymRecord> gymRecords = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "payment-plan-id", nullable = false)
    private PaymentPlan paymentPlan;
}
