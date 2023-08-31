package com.tc.training.smallFinance.exception;

public class KycNotCompletedException extends RuntimeException {
    public KycNotCompletedException(String completeKyc) {
        super(completeKyc);
    }
}
