package edu.utsa.cs3443.boxinggymapp.repository;

import edu.utsa.cs3443.boxinggymapp.model.PaymentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentPlanRepository extends JpaRepository<PaymentPlan, Integer> {
    PaymentPlan findByName(String name);
}
