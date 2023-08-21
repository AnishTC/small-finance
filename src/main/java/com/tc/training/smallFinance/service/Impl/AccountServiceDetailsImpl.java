package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AccountServiceDetailsImpl implements AccountServiceDetails {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    private static long lastTimestamp = System.currentTimeMillis();
    private static int sequence = 0;

    @Override
    public AccountDetailsOutputDto createAccount(AccountDetailsInputDto accountDetailsInputDto) {
        userService.addUser(accountDetailsInputDto);
        AccountDetails accountDetails = modelMapper.map(accountDetailsInputDto, AccountDetails.class);
        accountDetails.setAccountNumber(generateUniqueAccountNumber());
        accountRepository.save(accountDetails) ;
        return modelMapper.map(accountDetails,AccountDetailsOutputDto.class);
    }
    @Override
    public AccountDetailsOutputDto getAccount(Long accNo) {
        AccountDetails accountDetails =  accountRepository.getById(accNo);
        return modelMapper.map(accountDetails,AccountDetailsOutputDto.class);
    }

    public AccountDetailsOutputDto updateAccount(AccountDetailsInputDto accountDetailsInputDto, Long accountNumber){
        AccountDetails accountDetails = accountRepository.findById(accountNumber).orElseThrow(()-> new RuntimeException("account not found"));
        AccountDetails accountDetails1 = modelMapper.map(accountDetailsInputDto, AccountDetails.class);
        modelMapper.map(accountDetails1,accountDetails);
        accountDetails1 = accountRepository.save(accountDetails1);
        return modelMapper.map(accountDetails1,AccountDetailsOutputDto.class);
    }

    private synchronized Long generateUniqueAccountNumber() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp == lastTimestamp) {
            sequence++;
        } else {
            sequence = 0;
            lastTimestamp = currentTimestamp;
        }

        Long uniqueNumber = (Long) (currentTimestamp % 1000000000L * 10 + sequence) % 1000000000;
        return uniqueNumber;
    }


}
