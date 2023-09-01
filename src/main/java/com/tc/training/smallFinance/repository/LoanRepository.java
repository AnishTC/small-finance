package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.Loan;
import com.tc.training.smallFinance.utils.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    @Query(value = "select * from loan where account_account_number = ?1",nativeQuery = true)
    List<Loan> findAllByAccountNumber(Long accNo);

    @Query(value = "select * from loan where is_active = 1",nativeQuery = true)
    List<Loan> findByIsActive();

    @Query(value = "select * from loan where account_account_number = ?1 and is_active = 1",nativeQuery = true)
    List<Loan> findAllByAccountNumberAndIsActive(Long accNo);

    @Query(value = "select * from loan where status = 0",nativeQuery = true)
    List<Loan> findAllPending();

    @Query(value = "select * from loan where not status = 0",nativeQuery = true)
    List<Loan> findAllNotPending();
    @Query(value = "select * from loan where is_active = 1 and status = 1",nativeQuery = true)
    List<Loan> findByIsActiveAndAccepted();

    List<Loan> findByStatus(Status status);
}
