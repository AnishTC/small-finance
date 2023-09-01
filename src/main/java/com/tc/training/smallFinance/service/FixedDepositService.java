package com.tc.training.smallFinance.service;


import com.tc.training.smallFinance.dtos.inputs.FixedDepositInputDto;
import com.tc.training.smallFinance.dtos.outputs.FDDetails;
import com.tc.training.smallFinance.dtos.outputs.FixedDepositOutputDto;

import java.util.List;
import java.util.UUID;

public interface FixedDepositService {
    FixedDepositOutputDto createFixedDeposit(FixedDepositInputDto fixedDepositInputDto);

    List<FixedDepositOutputDto> getAllFixedDeposit(Long accNo);

    FDDetails getFDDetails(Long accNo);

    FixedDepositOutputDto breakFixedDeposit(String id);



    List<FixedDepositOutputDto> getAll();

    FixedDepositOutputDto getById(UUID id);

    List<FixedDepositOutputDto> getAllActive(Long accNo);
//    public FdOutputDto createFd(FdInputDto fdInputDto);
}
