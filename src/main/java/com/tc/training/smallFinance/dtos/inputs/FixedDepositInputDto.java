package com.tc.training.smallFinance.dtos.inputs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedDepositInputDto {

    private Long accountNumber;

    private String tenures;

    private Long amount;
}
