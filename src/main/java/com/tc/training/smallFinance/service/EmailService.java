package com.tc.training.smallFinance.service;

import org.springframework.stereotype.Service;


public interface EmailService {

    public void sendEmail(String to, String subject, String body);
}
