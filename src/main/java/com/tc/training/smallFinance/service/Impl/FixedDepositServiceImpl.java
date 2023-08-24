package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.FixedDepositInputDto;
import com.tc.training.smallFinance.dtos.outputs.FixedDepositOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.FixedDeposit;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.FixedDepositRepository;
import com.tc.training.smallFinance.repository.SlabRepository;
import com.tc.training.smallFinance.service.FixedDepositService;
import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FixedDepositServiceImpl implements FixedDepositService {
    @Autowired
    private FixedDepositRepository fixedDepositRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SlabRepository slabRepository;
    @Autowired
    private ModelMapper modelMapper;

    public FixedDepositOutputDto createFd(FixedDepositInputDto fixedDepositInputDto) {

        Long amount = fixedDepositInputDto.getAmount();
        AccountDetails accountNumber = accountRepository.findById(fixedDepositInputDto.getAccountNumber()).orElseThrow(); // add exception
        Tenures tenures = Tenures.valueOf(fixedDepositInputDto.getTenures());

        FixedDeposit fixedDeposit = new FixedDeposit();
        fixedDeposit.setAccountNumber(accountNumber);
        fixedDeposit.setAmount(amount);
        fixedDeposit.setSlabs(slabRepository.findslabs(tenures, TypeOfTransaction.FD));
        fixedDeposit.setDepositedDate(LocalDate.now());
        fixedDepositRepository.save(fixedDeposit);
        FixedDepositOutputDto fixedDepositOutputDto=modelMapper.map(fixedDeposit,FixedDepositOutputDto.class);
        fixedDepositOutputDto.setInterestRate(fixedDeposit.getSlabs().getIntrestRate());
        return fixedDepositOutputDto;
    }
}

