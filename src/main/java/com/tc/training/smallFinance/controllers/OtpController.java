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

    @PostMapping("/sendOtp")
    public ResponseEntity sendOtp(@RequestBody OtpInputDto otpInputDto){

        otpService.sendOtp(otpInputDto);
        return ResponseEntity.ok("otp sent");
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity verifyOtp(@RequestBody OtpInputDto otpInputDto){

       return  otpService.verifyOtp(otpInputDto);

    }
}
