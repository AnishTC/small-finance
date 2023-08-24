package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.model.Transaction;
import com.tc.training.smallFinance.utils.Role;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Data
public class LoginOutputDto {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Long balance;

    private Long loanAmount = 0L;

    private Long depositAmount=0L;

    private Long accNo;

    private Boolean kyc;

    private Role roleName;

   // private List<TransactionOutputDto> transactions;
}
