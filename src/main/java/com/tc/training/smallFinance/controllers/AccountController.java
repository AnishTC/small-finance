package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.service.Impl.AccountServiceDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Account")
public class AccountController {
    @Autowired
    private AccountServiceDetailsImpl accountServiceDetails;
    @PostMapping("/create")
    public ResponseEntity<AccountDetailsOutputDto> createAccount(@RequestBody AccountDetailsInputDto accountDetails){
        AccountDetailsOutputDto createdAccount=accountServiceDetails.createAccount(accountDetails);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }
    @GetMapping("getAccountDetails")
    public AccountDetailsOutputDto getAccountDetails(@RequestParam Long accountNumber){
        AccountDetailsOutputDto getAccount=accountServiceDetails.getAccount(accountNumber);
        return getAccount;
    }
}
