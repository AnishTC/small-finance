package com.tc.training.smallFinance.dtos.inputs;

import com.tc.training.smallFinance.utils.AccountType;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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

    private LocalDate openingDate;

    private LocalDate closingDate;

    private String phoneNumber;

    @Email
    private String email;

   /* private MultipartFile aadharPhoto;

    private MultipartFile panPhoto;

    private MultipartFile  userPhoto;
*/


}
