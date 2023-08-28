package com.tc.training.smallFinance.service.Impl;

import com.google.firebase.auth.UserRecord;
import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.inputs.FirebaseUserInputDto;

import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.EmailService;
import com.tc.training.smallFinance.service.FirebaseUserService;
import com.tc.training.smallFinance.service.UserService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
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
    @Autowired
    private FirebaseUserService firebaseUserService;

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
       // accountRepository.save(accountDetails) ;
        sendEmail(accountDetails.getUser().getEmail(),accountDetails.getUser().getPassword(),accountDetails.getAccountNumber());
        AccountDetailsOutputDto outputDto = modelMapper.map(accountDetails,AccountDetailsOutputDto.class);
        outputDto.setEmail(accountDetails.getUser().getEmail());


     //   AccountDetails accountDetails1 =modelMapper.map(accountDetails,AccountDetails.class);
        FirebaseUserInputDto inputDto = new FirebaseUserInputDto();
        inputDto.setAccountNumber(String.valueOf(accountDetails.getAccountNumber()));
//        inputDto.setAccountNumber(accountDetails.getAccountNumber());
        inputDto.setName(accountDetails.getUser().getFirstName());
        inputDto.setPassword(accountDetails.getUser().getPassword());

        UserRecord userInFireBase = firebaseUserService.createUserInFireBase(inputDto);
        accountDetails.getUser().setFirebaseId(userInFireBase.getUid());
        accountDetails =accountRepository.save(accountDetails);
        AccountDetailsOutputDto accountDetailsOutputDto =  modelMapper.map(accountDetails,AccountDetailsOutputDto.class);
        accountDetailsOutputDto.setEmail(accountDetails.getUser().getEmail());
        return accountDetailsOutputDto;
    }



//    User user1 = modelMapper.map(user, User.class);

//    FirebaseUserInputDto inputDto = new FirebaseUserInputDto();

//inputDto.setEmail(user1.getEmail()); //setting the login details given by the user to the firebase inputDto

//inputDto.setName(user1.getName());

//    String randomPassword = RandomStringUtils.randomAlphanumeric(9); //generating a random password of length 9 characters
//log.info(randomPassword);
//inputDto.setPassword(randomPassword);
//
//    UserRecord userInFireBase = firebaseUserService.createUserInFireBase(inputDto);
//user1.setFirebaseId(userInFireBase.getUid()); //creating user in firebase
//    user1 = userRepository.save(user1);
//return modelMapper.map(user1, UserOutputDto.class);









    @Override
    public AccountDetailsOutputDto getAccount(Long accNo) {
        AccountDetails accountDetails =  accountRepository.getById(accNo);
        return modelMapper.map(accountDetails,AccountDetailsOutputDto.class);
    }

    @Override
    public Double getBalance(Long accNo) {
        return accountRepository.findById(accNo).get().getBalance();
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
