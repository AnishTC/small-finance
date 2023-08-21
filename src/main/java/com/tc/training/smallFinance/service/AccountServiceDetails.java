package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;

public interface AccountServiceDetails {
    public AccountDetailsOutputDto createAccount(AccountDetailsInputDto accountDetailsInputDto);

    public AccountDetailsOutputDto getAccount(Long accountNumber);
}
