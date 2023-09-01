package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.dtos.outputs.HomePageOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.Impl.AccountServiceDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("Account")
public class AccountController {
    @Autowired
    private AccountServiceDetails accountServiceDetails;
    @PostMapping(value = "/create" ,consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AccountDetailsOutputDto> createAccount(@RequestBody AccountDetailsInputDto accountDetails){
        AccountDetailsOutputDto createdAccount=accountServiceDetails.createAccount(accountDetails);
        return ResponseEntity.ok(createdAccount);
    }
    @GetMapping("getAccountDetails")
    public AccountDetailsOutputDto getAccountDetails(@RequestParam Long accountNumber){
        AccountDetailsOutputDto getAccount=accountServiceDetails.getAccount(accountNumber);
        return getAccount;
    }
    @GetMapping("/getBalance")
    public Double getBalance(@RequestParam Long accNo){

        return accountServiceDetails.getBalance(accNo);
    }
    @GetMapping("/getAccountByUser")
    public AccountDetailsOutputDto getAccountByUser(@RequestParam UUID userId){
       return  accountServiceDetails.getAccountByUser(userId);
    }

    @GetMapping("/homePage")
    public HomePageOutputDto getHomePageDetails(@RequestParam Long accNo){

        return accountServiceDetails.getHomePageDetails(accNo);

    }
    @GetMapping("/setKyc")
    public AccountDetailsOutputDto verifyKyc(@RequestParam Long accNo){
        return accountServiceDetails.verifyKyc(accNo);
    }

}
