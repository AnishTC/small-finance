package com.tc.training.smallFinance.dtos.inputs;

import com.tc.training.smallFinance.utils.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDetailsInputDto {

    private String firstName;

    private String lastName;

   // private AccountType accountType;

    private LocalDate dob;

    private String aadharCardNumber;

    private String panCardNumber;

    private Date openingDate;

    private Date closingDate;

    private Integer phoneNumber;

    private String email;



}
