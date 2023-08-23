package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.FixedDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FdRepository extends JpaRepository<FixedDeposit,Long> {

}
