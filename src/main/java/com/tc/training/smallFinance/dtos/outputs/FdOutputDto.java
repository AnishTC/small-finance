package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.model.AccountDetails;
import lombok.Data;

@Data
public class FdOutputDto {
    private Long amount;

    private String interestRate;

    private Long accountNumber;

}
