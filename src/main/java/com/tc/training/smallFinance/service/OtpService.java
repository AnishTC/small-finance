package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.OtpInputDto;

public interface OtpService {
    void sendOtp(OtpInputDto otpInputDto);
}
