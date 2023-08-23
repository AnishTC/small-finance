package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "select * from user e where e.email = ?1",nativeQuery = true)
    User findByEmail(String userName);
    @Query(value = "select * from user e where e.first_name = ?1",nativeQuery = true)
    User findByName(String userName);
}
