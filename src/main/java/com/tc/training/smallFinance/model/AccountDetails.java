package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AccountDetails {

    @Id
    private Long accountNumber;

    private AccountType accountType = AccountType.Savings;

    private LocalDate openingDate ;

    private LocalDate closingDate;

//<<<<<<< HEAD
//=======

    private Double balance = 0D;
//>>>>>>> 751637a6a9a5756d549b84e369c5b386bab3a6ee

    private Boolean kyc = Boolean.FALSE;

    @OneToOne
    @JoinColumn(referencedColumnName = "userId",name="user_Id")
    private User user;

}
