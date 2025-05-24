package edu.utsa.cs3443.boxinggymapp.repository;

import edu.utsa.cs3443.boxinggymapp.model.GymMember;
import edu.utsa.cs3443.boxinggymapp.model.GymOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GymMemberRepository extends JpaRepository<GymMember, Integer> {
    Optional<GymMember> findByUsername(String username);

    List<GymMember> findByGymOwnerAndUsernameContainingIgnoreCase(GymOwner gymOwner, String query);

    @Query("SELECT m FROM GymMember m JOIN m.gymOwner go LEFT JOIN m.gymRecords gr " +
            "WHERE go.id = :id " +
            "AND (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:paid IS NULL OR m.paid = :paid) " +
            "AND (:weightMin IS NULL OR m.weight >= :weightMin) " +
            "AND (:weightMax IS NULL OR m.weight <= :weightMax) " +
            "AND (:paymentPlanName IS NULL OR m.paymentPlan.name = :paymentPlanName) " +
            "AND ((CAST(:startOfDay AS timestamp) IS NULL AND CAST(:endOfDay AS timestamp) IS NULL) OR gr.recordedAt BETWEEN :startOfDay AND :endOfDay)")
    List<GymMember> findMembersWithFilters(
            @Param("id") Integer gymOwnerId,
            @Param("name") String name,
            @Param("paid") Boolean paid,
            @Param("weightMin") Double weightMin,
            @Param("weightMax") Double weightMax,
            @Param("paymentPlanName") String paymentPlanName,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    Optional<GymMember> findByStripeCustomerId(String customerId);
}
