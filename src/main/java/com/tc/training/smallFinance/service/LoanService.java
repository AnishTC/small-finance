package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.LoanInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoanOutputDto;
import com.tc.training.smallFinance.model.Loan;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanOutputDto addLoan(LoanInputDto loanInputDto);

    LoanOutputDto getById(UUID id);

    List<LoanOutputDto> getAllByUser(Long accNo);

    List<LoanOutputDto> getAll();

    LoanOutputDto setLoan(UUID id,String status);

    ResponseEntity repay(UUID loanId, Double paymentAmount);
}
