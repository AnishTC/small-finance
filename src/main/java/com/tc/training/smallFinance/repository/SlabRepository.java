package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.Slabs;
import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SlabRepository extends JpaRepository<Slabs, UUID> {
   // @Query(value = "Select * from Slabs where tenures =1? && typeOfTransaction =2?",nativeQuery = true)
    public Slabs findByTenuresAndTypeOfTransaction(Tenures tenures, TypeOfTransaction typeOfTransaction);


}
