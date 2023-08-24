package com.tc.training.smallFinance.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalException {
    @ExceptionHandler(AmountNotSufficientException.class)
    public String handleAmountNotSufficientException(AmountNotSufficientException amountNotSufficientException){
        return amountNotSufficientException.getMessage();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public String handleAccountNotFound(AccountNotFoundException accountNotFoundException){
        return accountNotFoundException.getMessage();
    }

    @ExceptionHandler(UserNotFound.class)
    public String handleUserNotFound(UserNotFound userNotFound){
        return userNotFound.getMessage();
    }

    @ExceptionHandler(MyMailException.class)
    public String handleMyMailException(MyMailException myMailException){
        return myMailException.getMessage();
    }
}
