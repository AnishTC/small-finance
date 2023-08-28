package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.RecurringDepositInputDto;
import com.tc.training.smallFinance.dtos.outputs.RecurringDepositOutputDto;
import com.tc.training.smallFinance.service.RecurringDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rd")
public class RecurringDepositController {
    @Autowired
    private RecurringDepositService recurringDepositService;

    @PostMapping("/save")
    public RecurringDepositOutputDto saveRd(@RequestBody RecurringDepositInputDto recurringDepositInputDto){

        return recurringDepositService.saveRd(recurringDepositInputDto);

    }

    @GetMapping("/getById")
    public RecurringDepositOutputDto getById(@RequestParam UUID id){
        return recurringDepositService.getById(id);
    }

    @GetMapping("/getAll")
    public List<RecurringDepositOutputDto> getAll(){
        return recurringDepositService.getAll();
    }

    @PostMapping("/monthlyPay")
    public ResponseEntity monthlyPay(@RequestParam UUID id){
        return recurringDepositService.monthlyPay(id);
    }

    @GetMapping("/getTotalMoneyInvested")
    public Double getTotalMoneyInvested(@RequestParam Long accNo){
        return recurringDepositService.getTotalMoneyInvested(accNo);
    }
}
