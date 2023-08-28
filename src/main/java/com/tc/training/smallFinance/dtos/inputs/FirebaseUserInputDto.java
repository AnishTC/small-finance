package com.tc.training.smallFinance.dtos.inputs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseUserInputDto {
    @NotBlank
    private String name;

//    private String email;

    private String accountNumber;

    private String password;
}
