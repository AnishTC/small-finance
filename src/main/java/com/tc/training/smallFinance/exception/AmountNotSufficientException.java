package com.tc.training.smallFinance.exception;

public class AmountNotSufficientException extends RuntimeException{

    public AmountNotSufficientException(String message){

        super(message);
    }
}
