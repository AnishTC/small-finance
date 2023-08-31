package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;

public interface AccountServiceDetails {
    public AccountDetailsOutputDto createAccount(AccountDetailsInputDto accountDetailsInputDto);

    public AccountDetailsOutputDto getAccount(Long accountNumber);

    Double getBalance(Long accNo);
}
