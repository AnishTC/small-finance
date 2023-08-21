package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    private LocalDate openingDate;

    private LocalDate closingDate;

    private Long balance;

    private boolean kyc;

    @OneToOne
    @JoinColumn(referencedColumnName = "userId",name="user_Id")
    private User user;


}
