package com.tc.training.smallFinance.dtos.inputs;

import lombok.Data;

@Data
public class OtpInputDto {

    private String otp;

    private Long accountNumber;
}
