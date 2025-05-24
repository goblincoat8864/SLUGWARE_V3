package edu.utsa.cs3443.boxinggymapp.repository;

import edu.utsa.cs3443.boxinggymapp.model.GymOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GymOwnerRepository extends JpaRepository<GymOwner, Integer> {
    Optional<GymOwner> findByUsername(String username);

    Optional<GymOwner> findByStripeCustomerId(String customerId);
}
