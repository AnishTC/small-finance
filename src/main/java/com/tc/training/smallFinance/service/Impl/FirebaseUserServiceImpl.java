package com.tc.training.smallFinance.service.Impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.tc.training.smallFinance.dtos.inputs.FirebaseUserInputDto;
import com.tc.training.smallFinance.service.FirebaseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class FirebaseUserServiceImpl implements FirebaseUserService {

    @Override
    public UserRecord createUserInFireBase(FirebaseUserInputDto input) {
        log.debug("Trying to create user in firebase");
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(input.getAccountNumber() + "@aba.com") //getAccountNumber() + "@aba.com"
                .setPassword(input.getPassword())
                .setDisplayName(input.getName())
                .setDisabled(false);
        try {
            return FirebaseAuth.getInstance().createUser(request);
        } catch (FirebaseAuthException e) {
            log.error("Something went wrong : {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}