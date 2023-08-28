package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.OtpInputDto;
import org.springframework.http.ResponseEntity;

public interface OtpService {
    void sendOtp(OtpInputDto otpInputDto);

    ResponseEntity verifyOtp(OtpInputDto otpInputDto);
}
