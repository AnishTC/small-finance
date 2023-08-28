package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.RecurringDepositInputDto;
import com.tc.training.smallFinance.dtos.outputs.RecurringDepositOutputDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface RecurringDepositService {
    RecurringDepositOutputDto saveRd(RecurringDepositInputDto recurringDepositService);

    RecurringDepositOutputDto getById(UUID id);

    List<RecurringDepositOutputDto> getAll();

    ResponseEntity monthlyPay(UUID id);

    List<RecurringDepositOutputDto> getAllRecurringDeposit(Long accNo);

    Double getTotalMoneyInvested(Long accNo);
}
