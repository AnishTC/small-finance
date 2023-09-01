package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.LoanInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoanOutputDto;
import com.tc.training.smallFinance.model.Loan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanOutputDto addLoan(LoanInputDto loanInputDto);

    LoanOutputDto getById(UUID id);

    List<LoanOutputDto> getAllByUser(Long accNo);

    List<LoanOutputDto> getAll();

    LoanOutputDto setLoan(UUID id,String status);

    ResponseEntity repay(UUID loanId, Double paymentAmount);

    List<LoanOutputDto> getBytype(Long accNo, String type);

    Double getTotalLoanAmount(Long accNo);

    List<LoanOutputDto> getAllPending();

    List<LoanOutputDto> getAllByNotPending();

    void uploadSuppliment(MultipartFile file1, MultipartFile file2, UUID id);

    List<LoanOutputDto> getAllByStatus(String s);
}
