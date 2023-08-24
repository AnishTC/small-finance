package com.tc.training.smallFinance.dtos.outputs;

import lombok.Data;

@Data
public class FixedDepositOutputDto {
    private Long amount;

    private String interestRate;

    private Long accountNumber;

}
