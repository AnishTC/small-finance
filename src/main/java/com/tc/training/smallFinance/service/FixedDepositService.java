package com.tc.training.smallFinance.service;


import com.tc.training.smallFinance.dtos.inputs.FixedDepositInputDto;
import com.tc.training.smallFinance.dtos.outputs.FixedDepositOutputDto;

public interface FixedDepositService {
    FixedDepositOutputDto createFixedDeposit(FixedDepositInputDto fixedDepositInputDto);
//    public FdOutputDto createFd(FdInputDto fdInputDto);
}
