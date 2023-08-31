package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.RecurringDepositInputDto;
import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.RecurringDepositOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.model.*;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.RecurringDepositRepository;
import com.tc.training.smallFinance.repository.SlabRepository;
import com.tc.training.smallFinance.service.RecurringDepositService;
import com.tc.training.smallFinance.service.TransactionService;
import com.tc.training.smallFinance.utils.PaymentStatus;
import com.tc.training.smallFinance.utils.RdStatus;
import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecurringDepositServiceImpl implements RecurringDepositService {
   @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RecurringDepositRepository recurringDepositRepository;
    @Autowired
    private SlabRepository slabRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionService transactionService;

    @Override
    public RecurringDepositOutputDto saveRd(RecurringDepositInputDto recurringDepositInputDto) {

        RecurringDeposit rd = modelMapper.map(recurringDepositInputDto,RecurringDeposit.class);
        rd.setAccount(accountRepository.findById(recurringDepositInputDto.getAccountNumber()).orElseThrow(()->new AccountNotFoundException("account with this id not found")));
        Slabs slab = slabRepository.findByTenuresAndTypeOfTransaction(Tenures.ONE_YEAR, TypeOfTransaction.RD);
        rd.setInterest(slab.getInterestRate());
        rd.setStartDate(LocalDate.now());
        rd.setMaturityDate(rd.getStartDate().plusMonths(rd.getMonthTenure()));
        rd.setRdPayments(setPaymentSchedule(rd));

        rd = recurringDepositRepository.save(rd);
        RecurringDepositOutputDto rdout = modelMapper.map(rd, RecurringDepositOutputDto.class);
        rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));

        return rdout;
    }

    private List<RecurringDepositPayment> setPaymentSchedule(RecurringDeposit recurringDeposit) {

        List<RecurringDepositPayment> list = new ArrayList<>();
        for(int i=0;i<recurringDeposit.getMonthTenure();i++) {
            RecurringDepositPayment rd = new RecurringDepositPayment();
            rd.setRecurringDeposit(recurringDeposit);
            rd.setPayAmount(recurringDeposit.getMonthlyPaidAmount());
            rd.setMonthNumber(i+1);
            list.add(rd);
        }

        return list;

    }

    @Override
    public RecurringDepositOutputDto getById(UUID id) {
        RecurringDeposit rd = recurringDepositRepository.findById(id).orElseThrow(()-> new AccountNotFoundException("rd account not found"));
        RecurringDepositOutputDto rdout =  modelMapper.map(rd, RecurringDepositOutputDto.class);
        rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));
        return rdout;
    }

    @Override
    public List<RecurringDepositOutputDto> getAll() {

        List<RecurringDeposit> rds = recurringDepositRepository.findAll();
        List<RecurringDepositOutputDto> rdouts = new ArrayList<>();
        for(RecurringDeposit r:rds){
            RecurringDepositOutputDto rd = modelMapper.map(r, RecurringDepositOutputDto.class);
            rd.setAccount(String.valueOf(r.getAccount().getAccountNumber()));
            rdouts.add(rd);
        }
        Collections.sort(rdouts, new Comparator<RecurringDepositOutputDto>(){

            @Override
            public int compare(RecurringDepositOutputDto o1, RecurringDepositOutputDto o2) {
                if(o1.getStatus() .equals(RdStatus.ACTIVE) && o2.getStatus() .equals(RdStatus.ACTIVE)) return 0;
                else if(o1.getStatus() .equals(RdStatus.ACTIVE) && o2.getStatus() .equals(RdStatus.CLOSED)) return 1;
                else if(o1.getStatus() .equals(RdStatus.ACTIVE) && o2.getStatus() .equals(RdStatus.MATURED)) return 1;
                else return -1;
            }
        });
        return rdouts;
    }

    @Override
    public ResponseEntity monthlyPay(UUID id) {

        RecurringDeposit rd = recurringDepositRepository.findById(id).orElseThrow(()->new AccountNotFoundException("wrong id"));
        List<RecurringDepositPayment> recurringDepositPayments = rd.getRdPayments();
        Period period = Period.between(rd.getStartDate(), LocalDate.now());
        Integer monthNumber = period.getYears() * 12 + period.getMonths() + 1;
        RecurringDepositPayment repayment = null;
        for(RecurringDepositPayment r: recurringDepositPayments) {
            if (r.getMonthNumber() == monthNumber) {
                repayment = r;
                break;
            }
        }

        if(repayment == null) throw new AccountNotFoundException("RD term is over");

     //   if(repayment.getPaymentStatus().equals(PaymentStatus.PAID))  repayment.setPayAmount(repayment.getPayAmount()+rd.getMonthlyPaidAmount());

        UUID tId = setTransaction(rd,"DEBIT",rd.getMonthlyPaidAmount());
        repayment.setTransactionId(tId);
        repayment.setPaymentStatus(PaymentStatus.PAID);
        recurringDepositRepository.save(rd);
        if(monthNumber == rd.getRdPayments().size()) closeAccount(rd,"matured");

        return ResponseEntity.ok("RD monthly amount of "+rd.getMonthlyPaidAmount()+"  paid");
    }

    @Override
    public List<RecurringDepositOutputDto> getAllRecurringDeposit(Long accNo) {
        List<RecurringDeposit> rdList = recurringDepositRepository.findByAccount(accNo);
        List<RecurringDepositOutputDto> rdoutList = new ArrayList<>();
        for(RecurringDeposit rd : rdList){
            RecurringDepositOutputDto rdout = modelMapper.map(rd, RecurringDepositOutputDto.class);
            rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));
            rdoutList.add(rdout);
        }
        return rdoutList;
    }

    @Override
    public Double getTotalMoneyInvested(Long accNo) {
        List<RecurringDeposit> rds = recurringDepositRepository.findByAccount(accNo);
        Double sum=0D;
        for(RecurringDeposit r : rds){
            List<RecurringDepositPayment> rpay = r.getRdPayments();
            for(RecurringDepositPayment recurringDepositPayment : rpay){
                if(recurringDepositPayment.getPaymentStatus().equals(PaymentStatus.PAID)) sum+=recurringDepositPayment.getPayAmount();
            }
        }
        return sum;
    }

    private UUID setTransaction(RecurringDeposit rd, String status, Double amount){

        TransactionInputDto transaction = new TransactionInputDto();
        if(status.equals("CREDIT")) {
            //transaction.setFrom(loan.getAccount());
            transaction.setType("RD");
            // transaction.setAmount(loan.getLoanedAmount());
            transaction.setAmount(amount);
            transaction.setAccountNumber(String.valueOf(rd.getAccount().getAccountNumber()));
            transaction.setTo(String.valueOf(rd.getAccount().getAccountNumber()));
            transaction.setPurpose("RD amount credited");
        }
        else{
            //transaction.setFrom(loan.getAccount());
            transaction.setType("RD");
            transaction.setAmount(amount);
            transaction.setAccountNumber(String.valueOf(rd.getAccount().getAccountNumber()));
            // transaction.setTo(String.valueOf(loan.getAccount().getAccountNumber()));
            transaction.setPurpose("RD amount debited");
        }
        transaction.setTrans(status);
        UUID tId = transactionService.deposit(transaction,rd.getAccount().getAccountNumber()).getTransactionID();
        return tId;
    }

    private void scheduleCheck(){

        List<RecurringDeposit> rdList = recurringDepositRepository.findByIsActive();

        for(RecurringDeposit rd : rdList){

            List<RecurringDepositPayment> repaymentList = rd.getRdPayments();
            Period period = Period.between(rd.getStartDate(),LocalDate.now());
            Integer month = period.getYears() * 12 + period.getMonths();
            Integer count = 0;
            for(RecurringDepositPayment rdp : repaymentList){

                if(rdp.getMonthNumber() == month-1){
                    if(rdp.getPaymentStatus().equals(PaymentStatus.UPCOMING)) rdp.setPaymentStatus(PaymentStatus.UNPAID);
                }

                if(rdp.getPaymentStatus().equals(PaymentStatus.UNPAID)) count++;

            }

            Integer penaltyMonths = repaymentList.size() * 20 / 100;
            if(count > penaltyMonths) closeAccount(rd,"closed");

        }

}

    private void closeAccount(RecurringDeposit rd,String status) {

        if(status.equals("closed")) rd.setStatus(RdStatus.CLOSED);
        else rd.setStatus(RdStatus.MATURED);

        recurringDepositRepository.save(rd);
    }
    }
