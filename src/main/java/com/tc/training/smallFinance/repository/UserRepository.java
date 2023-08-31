package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "select * from user e where e.email = ?1",nativeQuery = true)
    User findByEmail(String userName);
    @Query(value = "select * from user e where e.account_number = ?1",nativeQuery = true)
    User findByAccountNumber(String accountNumber);
    @Query(value = "select * from user e where e.first_name = ?1",nativeQuery = true)
    User findByName(String userName);
    @Query(value = "select * from user e where e.firebase_id=?1",nativeQuery = true)
    User findByFirebaseId(String user_id);
}
