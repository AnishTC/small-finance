package com.tc.training.smallFinance.service;

import com.google.firebase.auth.UserRecord;
import com.tc.training.smallFinance.dtos.inputs.FirebaseUserInputDto;

public interface FirebaseUserService {
    UserRecord createUserInFireBase(FirebaseUserInputDto input);
}
