package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.utils.AccountType;

import java.util.Date;

public class AccountDetailsOutputDto {
    private Long accountNumber;
    private String accountHolderName;
    private AccountType accountType;
    private Date oppeningDate;
    private Date closingDate;
}
