package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountDetails,Long> {
<<<<<<< HEAD

    AccountDetails findByUser(User user);
=======


    AccountDetails findByUser(User user);

>>>>>>> Anish
}
