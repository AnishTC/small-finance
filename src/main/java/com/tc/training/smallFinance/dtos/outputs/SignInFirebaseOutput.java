package com.tc.training.smallFinance.dtos.outputs;

import lombok.Data;

@Data
public class SignInFirebaseOutput {
    public String idToken;
    public String email;
    public String refreshToken;
    public String expiresIn;
    public String localId;
    public boolean registered;
}
