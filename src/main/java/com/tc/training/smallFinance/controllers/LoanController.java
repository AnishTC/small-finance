package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.LoanInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoanOutputDto;
import com.tc.training.smallFinance.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loan")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public LoanOutputDto addLoan(@RequestBody LoanInputDto loanInputDto){
        return loanService.addLoan(loanInputDto);
    }
    @PostMapping("/check")
    public ResponseEntity<Object> check(@RequestBody LoanInputDto ch)
    {
        return  ResponseEntity.ok(ch);
    }

    @GetMapping("/getById")
    public LoanOutputDto getAll(@RequestParam UUID id){
        return loanService.getById(id);
    }

    @GetMapping("/getAllByUser")
    public List<LoanOutputDto> getAllByUser(@RequestParam(required = false) Long accNo){
        return loanService.getAllByUser(accNo);
    }

    @GetMapping("/getAll")
    public List<LoanOutputDto> getAll(){
        return loanService.getAll();
    }

    @PutMapping("/set")
    public LoanOutputDto setLoan(@RequestParam UUID id,@RequestParam String status){
       return loanService.setLoan(id,status);
    }

    @PostMapping("/repay")
    public ResponseEntity repayLoan(@RequestParam UUID loanId, @RequestParam Double paymentAmount){
        return loanService.repay(loanId,paymentAmount);
    }

}