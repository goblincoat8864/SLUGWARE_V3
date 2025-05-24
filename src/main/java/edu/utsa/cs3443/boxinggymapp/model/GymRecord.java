package edu.utsa.cs3443.boxinggymapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "gym-records")
@Getter
@Setter
public class GymRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym-member-id", nullable = false)
    private GymMember gymMember;

    @ManyToOne
    @JoinColumn(name = "gym-owner-id", nullable = false)
    private GymOwner gymOwner;

    @Column(name = "recorded-at", nullable = false)
    private LocalDateTime recordedAt;
}

