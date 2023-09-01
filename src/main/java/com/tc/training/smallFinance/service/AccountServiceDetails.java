package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.dtos.outputs.HomePageOutputDto;

import java.util.UUID;

public interface AccountServiceDetails {
    public AccountDetailsOutputDto createAccount(AccountDetailsInputDto accountDetailsInputDto);

    public AccountDetailsOutputDto getAccount(Long accountNumber);

    Double getBalance(Long accNo);

    AccountDetailsOutputDto getAccountByUser(UUID userId);

    HomePageOutputDto getHomePageDetails(Long accNo);

    AccountDetailsOutputDto verifyKyc(Long accNo);
}
