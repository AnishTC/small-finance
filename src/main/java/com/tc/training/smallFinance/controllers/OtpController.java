package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.OtpInputDto;
import com.tc.training.smallFinance.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OtpController {
    @Autowired
    private OtpService otpService;

    public ResponseEntity sendOtp(@RequestParam OtpInputDto otpInputDto){

        otpService.sendOtp(otpInputDto);
        return ResponseEntity.ok("otp sent");
    }
}
