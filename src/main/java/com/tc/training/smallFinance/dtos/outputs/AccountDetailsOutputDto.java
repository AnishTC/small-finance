package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.utils.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsOutputDto {

    private Long accountNumber;

    private AccountType accountType;

    private String email;

    private Long balance;

    private Boolean kyc;


}
