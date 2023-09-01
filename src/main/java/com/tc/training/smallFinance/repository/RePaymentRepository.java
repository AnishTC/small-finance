package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RePaymentRepository extends JpaRepository<Repayment, UUID> {
}
