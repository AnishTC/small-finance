package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.OtpInputDto;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.EmailService;
import com.tc.training.smallFinance.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void sendOtp(OtpInputDto otpInputDto) {

        User user = accountRepository.findById(otpInputDto.getAccountNumber()).get().getUser();
        String to = user.getEmail();
        String subject = "Otp for transaction";
        String body = "Your otp is "+otpInputDto.getOtp();
        emailService.sendEmail(to,subject,body);
    }
}
