package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.dtos.outputs.RecurringDepositOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.RecurringDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit, UUID> {

    @Query(value = "select * from recurring_deposit where status = ACTIVE",nativeQuery = true)
    List<RecurringDeposit> findByIsActive();

    @Query(value = "select * from recurring_deposit where account_account_number = ?1",nativeQuery = true)
    List<RecurringDeposit> findByAccount(Long accountNumber);
}
