package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.LoanInputDto;
import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoanOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.model.Loan;
import com.tc.training.smallFinance.model.Repayment;
import com.tc.training.smallFinance.model.Transaction;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.LoanRepository;
import com.tc.training.smallFinance.repository.SlabRepository;
import com.tc.training.smallFinance.service.LoanService;
import com.tc.training.smallFinance.service.TransactionService;
import com.tc.training.smallFinance.utils.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SlabRepository slabRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TransactionService transactionService;

    @Override
    public LoanOutputDto addLoan(LoanInputDto loanInputDto) {

        Loan loan = new Loan();
        loan.setLoanedAmount(loanInputDto.getLoanAmount());
        loan.setAccount(accountRepository.findById(Long.valueOf(loanInputDto.getAccountNumber())).orElseThrow(()->new AccountNotFoundException("account not found")));
        loan.setAppliedDate(LocalDate.now());
        loan.setLoanEndDate(loan.getAppliedDate().plusYears(Integer.parseInt(loanInputDto.getTenure())));


        if(loanInputDto.getType().equals("GOLD_LOAN")){

            loan.setTypeOfLoan(TypeOfLoans.GOLD_LOAN);
            loan.setSlab(slabRepository.findByTenuresAndTypeOfTransaction(Tenures.ONE_YEAR,TypeOfTransaction.GOLD_LOAN));

        }
        else if(loanInputDto.getType().equals("PERSONAL_LOAN")){
            loan.setTypeOfLoan(TypeOfLoans.PERSONAL_LOAN);
            loan.setSlab(slabRepository.findByTenuresAndTypeOfTransaction(Tenures.ONE_YEAR,TypeOfTransaction.PERSONAL_LOAN));

        }
        else if(loanInputDto.getType().equals("EDUCATION_LOAN")){
            loan.setTypeOfLoan(TypeOfLoans.EDUCATION_LOAN);
            loan.setSlab(slabRepository.findByTenuresAndTypeOfTransaction(Tenures.ONE_YEAR,TypeOfTransaction.EDUCATION_LOAN));

        }
        else if(loanInputDto.getType().equals("HOME_LOAN")) {
            loan.setTypeOfLoan(TypeOfLoans.HOME_LOAN);;
            loan.setSlab(slabRepository.findByTenuresAndTypeOfTransaction(Tenures.ONE_YEAR, TypeOfTransaction.HOME_LOAN));

        }

        loan.setInterest(loan.getSlab().getInterestRate());
        loanRepository.save(loan);
        return modelMapper.map(loan,LoanOutputDto.class);

    }


    @Override
    public LoanOutputDto getById(UUID id) {
        Loan loan = loanRepository.findById(id).orElseThrow(()->new AccountNotFoundException("loan account not found"));
        return modelMapper.map(loan,LoanOutputDto.class);
    }

    @Override
    public List<LoanOutputDto> getAllByUser(Long accNo) {
        List<Loan> list;
        if(accNo != null) list = loanRepository.findAllByAccountNumber(accNo);
        else  list = loanRepository.findAll();
        List<LoanOutputDto> newList = list.stream().map(loan -> modelMapper.map(loan,LoanOutputDto.class)).collect(Collectors.toList());
        Collections.sort(newList, Collections.reverseOrder(Comparator.comparing(LoanOutputDto::getIsActive)));
        return newList;
    }

    @Override
    public List<LoanOutputDto> getAll() {
        List<LoanOutputDto> newList =  loanRepository.findAll().stream().map(loan->modelMapper.map(loan,LoanOutputDto.class)).collect(Collectors.toList());
        Collections.sort(newList, Collections.reverseOrder(Comparator.comparing(LoanOutputDto::getIsActive)));
        return newList;
    }

    @Override
    public LoanOutputDto setLoan(UUID id,String status) {

        Loan loan = loanRepository.findById(id).orElseThrow(()->new AccountNotFoundException("account not found"));
        Period period = Period.between(loan.getAppliedDate(),loan.getLoanEndDate());
        int months = period.getYears() * 12 + period.getMonths();
        if(status.equals("APPROVE") && loan.getStatus()==Status.UNDER_REVIEW) {
            loan.setStatus(Status.APPROVED);
            loan.setRemainingAmount((loan.getLoanedAmount()*Double.valueOf(loan.getInterest())/100)+ loan.getLoanedAmount());
            loan.setRepayments(setRepayment(loan));
            loan.setMonthlyInterestAmount((int) Math.round(loan.getRepayments().get(1).getPayAmount()));
            setTransaction(loan,"CREDIT",loan.getLoanedAmount());
        }
        else if(status.equals("REJECT") && loan.getStatus()==Status.UNDER_REVIEW) {
            loan.setStatus(Status.REJECTED);
            loan = closeLoan(loan);
        }
        loanRepository.save(loan);
        return modelMapper.map(loan, LoanOutputDto.class);
    }

    @Override
    public ResponseEntity repay(UUID loanId, Double paymentAmount) {

        Loan loan = loanRepository.findById(loanId).orElseThrow(()->new AccountNotFoundException("no loan with such loanId"));
        Period period = Period.between(loan.getAppliedDate(), LocalDate.now());
        Integer monthNumber = period.getYears() * 12 + period.getMonths() + 1;
        List<Repayment> list = loan.getRepayments();
        Repayment repayment = null;
        for(Repayment r: loan.getRepayments()) {
            if (r.getMonthNumber() == monthNumber) {
                repayment = r;
                break;
            }
        }
        if(repayment == null) throw new AccountNotFoundException("Loan term is over");

        if(repayment.getPaymentStatus().equals(PaymentStatus.PAID))  repayment.setPayAmount(repayment.getPayAmount()+paymentAmount);

        if(paymentAmount == repayment.getPayAmount()) {
            setTransaction(loan,"DEBIT",paymentAmount);
            repayment.setPaymentStatus(PaymentStatus.PAID);
            loan.setRemainingAmount(loan.getRemainingAmount() - paymentAmount);
        }
      //  else if (paymentAmount > repayment.getPayAmount()) {
        else{
            setTransaction(loan,"DEBIT",paymentAmount);
            repayment.setPaymentStatus(PaymentStatus.PAID);
            loan.setRemainingAmount(loan.getRemainingAmount() - paymentAmount);
            loan.setRepayments(updateRepaymentSchedule(loan.getRepayments(),loan.getRemainingAmount(),monthNumber,Double.valueOf(loan.getInterest())));
        }
       /* else if(paymentAmount < repayment.getPayAmount()){
            setTransaction(loan,"DEBIT",paymentAmount);
            repayment.setPaymentStatus(PaymentStatus.PAID);
            loan.setRemainingAmount(loan.getRemainingAmount() - paymentAmount);
            loan.setRepayments(updateRepaymentSchedule(loan.getRepayments(),loan.getRemainingAmount(),monthNumber,Double.valueOf(loan.getInterest())));
        }*/

        for(Repayment r:loan.getRepayments()) {
            if(r.getPaymentStatus().equals(PaymentStatus.UPCOMING)){
                loan.setMonthlyInterestAmount((int)Math.round(r.getPayAmount()));
                break;
            }
        }


        if(monthNumber == loan.getRepayments().size() || loan.getRemainingAmount() == 0)  loan = closeLoan(loan);

        loanRepository.save(loan);

        return ResponseEntity.ok("Loan repayment updated for month " + monthNumber);

    }

    private Loan closeLoan(Loan loan) {

        loan.setIsActive(Boolean.FALSE);
        loan.setLoanEndDate(LocalDate.now());
        if(loan.getSlab().equals(Status.REJECTED)) {
            loan.setInterestAmount(0D);
            loan.setTotalAmount(0D);
        }
        else {
            loan.setInterestAmount(loan.getLoanedAmount()*Double.valueOf(loan.getInterest())/(loan.getRepayments().size()/12) - loan.getLoanedAmount());
            loan.setTotalAmount(loan.getLoanedAmount() + loan.getInterestAmount());
        }

        loan.setMonthlyInterestAmount(0);
        return loan;
    }


    private List<Repayment> updateRepaymentSchedule(List<Repayment> repayments, Double remainingAmount,Integer monthNumber,Double interest) {

        Integer count=repayments.size()-monthNumber;

       /* for(Repayment r:repayments){

            if(r.getMonthNumber()>monthNumber) count++;

        }*/

        Double monthlyAmount = monthlyPayAmount(remainingAmount,interest,count);

        System.out.println(remainingAmount+" "+interest+" "+monthlyAmount);

        for(Repayment r: repayments){

            if(r.getMonthNumber()>monthNumber) {
                r.setPayAmount(monthlyAmount);
                System.out.println("set");
            }
        }

        return repayments;

    }



    private List<Repayment> setRepayment(Loan loan){
        List<Repayment> list = new ArrayList<>();
        Period period = Period.between(loan.getAppliedDate(), loan.getLoanEndDate());
        int noOfMonths = period.getYears() * 12 + period.getMonths();
        Double monthlyPay = monthlyPayAmount(loan.getRemainingAmount(), Double.valueOf(loan.getInterest()),noOfMonths);
        for(int i=0;i<noOfMonths;i++) {
            Repayment repayment = new Repayment();
            repayment.setLoan(loan);
            repayment.setMonthNumber(i+1);
            repayment.setPayAmount(monthlyPay);
            list.add(repayment);
            System.out.println(repayment.getMonthNumber());
        }

        return list;
    }

    private Double monthlyPayAmount(Double remainingAmount,Double interest,Integer noOfMonths){
        Double si  = remainingAmount * (interest/100) * (noOfMonths/12);
        Double monthlyRepayment = (remainingAmount + si) / noOfMonths;
        System.out.println(monthlyRepayment);
        return monthlyRepayment;
    }

    private void setTransaction(Loan loan,String status,Double amount){

        TransactionInputDto transaction = new TransactionInputDto();
        if(status.equals("CREDIT")) {
            //transaction.setFrom(loan.getAccount());
            transaction.setType(loan.getTypeOfLoan().toString());
           // transaction.setAmount(loan.getLoanedAmount());
            transaction.setAmount(amount);
            transaction.setAccountNumber(String.valueOf(loan.getAccount().getAccountNumber()));
            transaction.setTo(String.valueOf(loan.getAccount().getAccountNumber()));
            transaction.setPurpose("Loan amount credited");
        }
        else{
            //transaction.setFrom(loan.getAccount());
            transaction.setType(loan.getTypeOfLoan().toString());
            transaction.setAmount(amount);
            transaction.setAccountNumber(String.valueOf(loan.getAccount().getAccountNumber()));
           // transaction.setTo(String.valueOf(loan.getAccount().getAccountNumber()));
            transaction.setPurpose("Loan interest debited");
        }
        transaction.setTrans(status);
        transactionService.deposit(transaction,loan.getAccount().getAccountNumber());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    private void scheduleCheck(){

        List<Loan> loanList = loanRepository.findByIsActive();

        for(Loan loan : loanList){

            List<Repayment> repaymentList = loan.getRepayments();
            Period period = Period.between(loan.getAppliedDate(),LocalDate.now());
            Integer month = period.getYears() * 12 + period.getMonths();
            Integer count = 0;
            for(Repayment r : repaymentList){

                if(r.getMonthNumber() == month-1){
                    if(r.getPaymentStatus().equals(PaymentStatus.UPCOMING)) r.setPaymentStatus(PaymentStatus.UNPAID);
                }

                if(r.getPaymentStatus().equals(PaymentStatus.UNPAID)) count++;

            }

            Integer penaltyMonths = repaymentList.size() * 20 / 100;
            if(count > penaltyMonths) {
                loan.setPenaltyInterest("1");
                loan.setRepayments(updateRepaymentSchedule(repaymentList,loan.getRemainingAmount(),month, Double.valueOf(loan.getInterest()) + Double.valueOf(loan.getPenaltyInterest())));
                loanRepository.save(loan);
            }

        }

    }



}
