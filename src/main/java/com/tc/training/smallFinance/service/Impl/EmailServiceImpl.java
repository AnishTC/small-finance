package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.exception.MyMailException;
import com.tc.training.smallFinance.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

       try {
           mailSender.send(message);
       }
       catch(MailException exception) {
           throw new MyMailException("mail not sent") ;

       }
    }
}
