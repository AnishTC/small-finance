package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.TransactionOutputDto;
import com.tc.training.smallFinance.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @PostMapping("/transfer")
    public TransactionOutputDto transfer(@RequestBody TransactionInputDto transactionInputDto, @RequestParam Long accNo){

        return transactionService.deposit(transactionInputDto,accNo);
    }

    @GetMapping("/allTransactions")
    public List<TransactionOutputDto> transactionHistory(@RequestParam(required = false)LocalDate date1, @RequestParam(required = false) LocalDate date2, @RequestParam Long accNo ){
       /* LocalDateTime localDateTime1= date1.atStartOfDay();
        LocalDateTime localDateTime2= LocalDateTime.of(date2, LocalTime.of(23,59,59));*/
        return transactionService.getAllTransactions(date1,date2,accNo);
    }
}
