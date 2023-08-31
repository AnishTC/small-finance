package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.outputs.FDDetails;
import com.tc.training.smallFinance.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deposit")
public class DepositController {
    @Autowired
    private DepositService  depositService;

    @GetMapping("/getDetails")
    public FDDetails getDetails(@RequestParam Long accNo){

       return depositService.getDetails(accNo);

    }
    @GetMapping("/get")
    public List<Object> getAccounts(@RequestParam Long accNo){

        return depositService.getAccounts(accNo);

    }
}
