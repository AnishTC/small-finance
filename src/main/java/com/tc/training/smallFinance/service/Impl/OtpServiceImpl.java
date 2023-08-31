package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.OtpInputDto;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.EmailService;
import com.tc.training.smallFinance.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountRepository accountRepository;

    private Long otp = 0L;

    Boolean otpActive = Boolean.TRUE;

    @Override
    public void sendOtp(OtpInputDto otpInputDto) {

        User user = accountRepository.findById(otpInputDto.getAccountNumber()).get().getUser();

        otp = Long.valueOf(generateOtp(6));

        String to = user.getEmail();
        String subject = "Otp for transaction";
        String body = "Your otp is "+otp;
        otpActive  = Boolean.TRUE;
        emailService.sendEmail(to,subject,body);
    }

    @Override
    public ResponseEntity verifyOtp(OtpInputDto otpInputDto) {
        ResponseEntity responseEntity;
        if(otpInputDto.getOtp().equals(otp) && otpActive.equals(Boolean.TRUE)) responseEntity =  ResponseEntity.ok("otp verified");
        else if(otpActive.equals(Boolean.FALSE))
        responseEntity =  new ResponseEntity("time out", HttpStatusCode.valueOf(200));
        else responseEntity =  new ResponseEntity("wrong otp", HttpStatusCode.valueOf(400));
        otp = 0L;
        return responseEntity;
    }

    public static String generateOtp(int length) {
        String numbers = "0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(numbers.length());
            otp.append(numbers.charAt(index));
        }

        return otp.toString();
    }

    @Scheduled(cron = " 0 */5 * * * *")
    private void cancelOtp(){

        otp = 0L;

    }

}
