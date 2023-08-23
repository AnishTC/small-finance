package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.EmailService;
import com.tc.training.smallFinance.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

@Service
public class AccountServiceDetailsImpl implements AccountServiceDetails {
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    private static long lastTimestamp = 1000000000000000L;

    private static int sequence = 0;

    @Override
    public AccountDetailsOutputDto createAccount(AccountDetailsInputDto accountDetailsInputDto) {

        AccountDetails accountDetails = modelMapper.map(accountDetailsInputDto, AccountDetails.class);
        accountDetails.setUser(userService.addUser(accountDetailsInputDto));
        accountDetails.setAccountNumber(generateUniqueAccountNumber());
        accountDetails.setOpeningDate(LocalDate.now());
       /* accountDetails.getUser().setAadharPhoto(encode(accountDetailsInputDto.getAadharPhoto()));
        accountDetails.getUser().setPanPhoto(encode(accountDetailsInputDto.getPanPhoto()));
        accountDetails.getUser().setUserPhoto(encode(accountDetailsInputDto.getUserPhoto()));*/
        accountRepository.save(accountDetails) ;
        sendEmail(accountDetails.getUser().getEmail(),accountDetails.getUser().getPassword(),accountDetails.getAccountNumber());
        AccountDetailsOutputDto outputDto = modelMapper.map(accountDetails,AccountDetailsOutputDto.class);
        outputDto.setEmail(accountDetails.getUser().getEmail());
        return outputDto;
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
        lastTimestamp++;
        return lastTimestamp;
    }

    private void sendEmail(String email, String password,Long accountNumber) {
        String subject = "Username and password for your account";
        String text = "Thank you for registering with our bank your account number is "+accountNumber+ " and your password is "+password;
        emailService.sendEmail(email,subject,text);
    }

    private String encode(MultipartFile file) {
        String b64="";
        try {
            byte[] b = file.getBytes();
            b64 = Base64.encodeBase64String(b);
        }
        catch(IOException e){}
        return b64;
    }




}
