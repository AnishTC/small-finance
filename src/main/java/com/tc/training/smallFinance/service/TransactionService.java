package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.TransactionOutputDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionOutputDto deposit(TransactionInputDto transactionInputDto,Long accountNumber);

    List<TransactionOutputDto> getAllTransactions(LocalDate date1, LocalDate date2, Long accNo);
}
