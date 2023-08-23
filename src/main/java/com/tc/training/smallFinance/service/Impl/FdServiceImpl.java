package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.FdInputDto;
import com.tc.training.smallFinance.dtos.outputs.FdOutputDto;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.FixedDeposit;
import com.tc.training.smallFinance.model.Slabs;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.FdRepository;
import com.tc.training.smallFinance.repository.SlabRepository;
import com.tc.training.smallFinance.service.FdService;
import com.tc.training.smallFinance.utils.Tenures;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FdServiceImpl implements FdService {
    @Autowired
    private FdRepository fdRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SlabRepository slabRepository;
    @Autowired
    private ModelMapper modelMapper;
    public FdOutputDto createFd(FdInputDto fdInputDto) {

        Long amount = fdInputDto.getAmount();
        AccountDetails accountNumber=accountRepository.findById(fdInputDto.getAccountNumber()).orElseThrow(); // add exception
        String tenures=fdInputDto.getTenures();

            FixedDeposit fd = new FixedDeposit();
            fd.setAccountNumber(accountNumber);
            fd.setAmount(amount);
            fd.setTenures(slabRepository.findBy);
            fd.setDepositedDate(LocalDate.now());
            fdRepository.save(fd);
        //Exception needed
//        else {
//            throw new AccountNotFoundException("Account not found");
//        }
        FdOutputDto fdOutputDto=modelMapper.map(fdInputDto,FdOutputDto.class);
        fdOutputDto.setInterestRate(fd.getTenures().ge)

        return

    }
}

