package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.outputs.FDDetails;
import com.tc.training.smallFinance.dtos.outputs.FixedDepositOutputDto;
import com.tc.training.smallFinance.dtos.outputs.RecurringDepositOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.FixedDeposit;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.DepositService;
import com.tc.training.smallFinance.service.FixedDepositService;
import com.tc.training.smallFinance.service.RecurringDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepositServiceImpl implements DepositService {
    @Autowired
    private FixedDepositService fixedDepositService;
    @Autowired
    private RecurringDepositService recurringDepositService;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public FDDetails getDetails(Long accNo) {

        FDDetails fdDetails = new FDDetails();
        List<FixedDepositOutputDto> fds = fixedDepositService.getAllFixedDeposit(accNo);
        List<RecurringDepositOutputDto> rds = recurringDepositService.getAllRecurringDeposit(accNo);
        fdDetails.setTotalNoOfFD(fds.size());
        fdDetails.setTotalNoOfRD(rds.size());
        Double fdSum =0D;
        Double rdSum = 0D;
        for(FixedDepositOutputDto fdout:fds){
            fdSum+=fdout.getAmount();
        }
        fdDetails.setTotalFdAmount(fdSum);
        fdDetails.setTotalRdAmount(recurringDepositService.getTotalMoneyInvested(accNo));
        return fdDetails;
    }

    @Override
    public List<Object> getAccounts(Long accNo) {

        List<Object> obj = new ArrayList<>();
        List<RecurringDepositOutputDto> rds = recurringDepositService.getAllRecurringDeposit(accNo);
        List<FixedDepositOutputDto> fds = fixedDepositService.getAllFixedDeposit(accNo);

        for(FixedDepositOutputDto fdout:fds) obj.add(fdout);
        for(RecurringDepositOutputDto rout:rds) obj.add(rout);

        return obj;
    }
}
