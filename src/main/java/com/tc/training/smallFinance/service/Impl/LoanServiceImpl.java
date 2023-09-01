package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.LoanInputDto;
import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoanOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.exception.MyMailException;
import com.tc.training.smallFinance.model.*;
import com.tc.training.smallFinance.repository.*;
import com.tc.training.smallFinance.service.*;
import com.tc.training.smallFinance.utils.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private UserService userService;
    @Autowired
    private AccountServiceDetails accountService;
    @Autowired
    private EmailService emailService;


    Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

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
        LoanOutputDto lout = modelMapper.map(loan, LoanOutputDto.class);
        lout.setTenure(loan.getSlab().getTenures().toString());
        return lout;
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
        Period period = Period.between(loan.getStartDate(),loan.getLoanEndDate());
        int months = period.getYears() * 12 + period.getMonths();
        if(status.equals("APPROVE") && loan.getStatus()==Status.UNDER_REVIEW) {
            loan.setStatus(Status.APPROVED);
            Double totalAmountToPay = (loan.getLoanedAmount()*(Double.valueOf(loan.getInterest())/100))*(months/12) + loan.getLoanedAmount();
            loan.setRemainingAmount(totalAmountToPay);
            loan.setNextPaymentDate(loan.getStartDate().plusMonths(1));
            loan.setStartDate(LocalDate.now());
            //loan.setRepayments(setRepayment(loan));
          //  loan.setRepayments(new ArrayList<>());
            loan.setMonthlyInterestAmount((int) Math.round(monthlyPayAmount(loan.getRemainingAmount(),months)));
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
        Period period = Period.between(loan.getStartDate(), LocalDate.now());
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
            repayment.setTransactionId(setTransaction(loan,"DEBIT",paymentAmount));
            repayment.setPaymentStatus(PaymentStatus.PAID);
            loan.setRemainingAmount(loan.getRemainingAmount() - paymentAmount);
            loan.setRepayments(updateRepaymentSchedule(loan.getRepayments(),loan.getRemainingAmount(),monthNumber,Double.valueOf(loan.getInterest())));
        }

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

    @Override
    public List<LoanOutputDto> getBytype(Long accNo, String type) {

        List<Loan> list = loanRepository.findAllByAccountNumber(accNo);
        List<LoanOutputDto> typeLoanList = new ArrayList<>();
        TypeOfLoans loanType = TypeOfLoans.valueOf(type);
        for(Loan l : list){

            if(l.getTypeOfLoan().equals(loanType)) typeLoanList.add(modelMapper.map(l,LoanOutputDto.class));

        }
        return typeLoanList;
    }

    @Override
    public Double getTotalLoanAmount(Long accNo) {
        List<Loan> loan = loanRepository.findAllByAccountNumberAndIsActive(accNo);
        Double amount = 0D;
        for(Loan l : loan){
            amount += l.getLoanedAmount();
        }
        return amount;
    }

    @Override
    public List<LoanOutputDto> getAllPending() {
        List<Loan> loans = loanRepository.findAllPending();
        return loans.stream().map(loan -> modelMapper.map(loan, LoanOutputDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<LoanOutputDto> getAllByNotPending() {
        List<Loan> loans = loanRepository.findAllNotPending();
        return loans.stream().map(loan -> modelMapper.map(loan, LoanOutputDto.class)).collect(Collectors.toList());
    }

    @Override
    public void uploadSuppliment(MultipartFile file1, MultipartFile file2, UUID id) {
        Loan loan = loanRepository.findById(id).orElseThrow(()->new AccountNotFoundException("account with this id  "));
        loan.setLoanSuppliment1(userService.uploadPic(file1));
        loan.setLoanSuppliment2(userService.uploadPic(file2));
    }

    @Override
    public List<LoanOutputDto> getAllByStatus(String s) {
        Status status = Status.valueOf(s);
        List<Loan> loans = loanRepository.findByStatus(status);
        List<LoanOutputDto> loanOutputDtos = new ArrayList<>();
        for(Loan loan : loans){
            LoanOutputDto lout = modelMapper.map(loan, LoanOutputDto.class);
            lout.setAccountNumber(String.valueOf(loan.getAccount().getAccountNumber()));
            lout.setTenure(loan.getSlab().getTenures().toString());
            loanOutputDtos.add(lout);
        }
        return loanOutputDtos;
    }

    private Loan closeLoan(Loan loan) {

        loan.setIsActive(Boolean.FALSE);
        loan.setLoanEndDate(LocalDate.now());
        if(loan.getStatus().equals(Status.REJECTED)) {
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

        Double monthlyAmount = monthlyPayAmount(remainingAmount,count);

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
        Period period = Period.between(loan.getStartDate(), loan.getLoanEndDate());
        int noOfMonths = period.getYears() * 12 + period.getMonths();
        Double monthlyPay = monthlyPayAmount(loan.getRemainingAmount(),noOfMonths);
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

    private Double monthlyPayAmount(Double remainingAmount,Integer noOfMonths){
        /*Double si  = remainingAmount * (interest/100) * (noOfMonths/12);
        Double monthlyRepayment = (remainingAmount + si) / noOfMonths;*/
        Double monthlyRepayment = remainingAmount/noOfMonths;
        System.out.println(monthlyRepayment);
        return monthlyRepayment;
    }

    private UUID setTransaction(Loan loan,String status,Double amount){

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
        return transactionService.deposit(transaction,loan.getAccount().getAccountNumber()).getTransactionID();
    }

   /* @Scheduled(cron = "0 0 0 * * *")
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

    }*/

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    private void diffSchedule() {
        logger.info("entered diff schedule");
        List<Loan> loans = loanRepository.findByIsActiveAndAccepted();
        for (Loan loan : loans) {
            if(!loan.getNextPaymentDate().equals(LocalDate.now())) continue;
            Period period = Period.between(loan.getStartDate(), LocalDate.now());
            Period noOfMonths = Period.between(loan.getStartDate(), loan.getLoanEndDate());
            Integer totalNoOfMonthsLeft = noOfMonths.getYears() * 12 + noOfMonths.getMonths();
            Integer month = period.getYears() * 12 + period.getMonths();
            Integer count = 0;
            Double monthlyPay = Double.valueOf(loan.getMonthlyInterestAmount());   //monthlyPayAmount(loan.getRemainingAmount(), totalNoOfMonthsLeft - month - 1);
            loan.setMonthlyInterestAmount((int) Math.round(monthlyPay));
            if(loan.getRepayments() == null) loan.setRepayments(new ArrayList<>());

            if (accountService.getBalance(loan.getAccount().getAccountNumber()) > monthlyPay) {
                Repayment repayment = new Repayment();
                repayment.setPayAmount(monthlyPay);
                repayment.setLoan(loan);
                repayment.setMonthNumber(month);
                repayment.setTransactionId(setTransaction(loan, "DEBIT", monthlyPay));
                repayment.setPaymentStatus(PaymentStatus.PAID);
                loan.getRepayments().add(repayment);
                loan.setRemainingAmount(loan.getRemainingAmount() - loan.getMonthlyInterestAmount());
                loan.setNextPaymentDate(loan.getStartDate().plusMonths(month+1));
                loan.setMonthlyInterestAmount((int) Math.round(monthlyPayAmount(loan.getRemainingAmount(), totalNoOfMonthsLeft - month - 1)));
            } else {
                try {
                    ResponseEntity rs = sendEmail(loan);
                }catch(MyMailException e) {}
                if(period.getDays()!=0 && loan.getMissedPaymentCount()<4){
                    loan.setMonthlyInterestAmount((int) Math.round(loan.getMonthlyInterestAmount() * 0.1));
                    loan.setMissedPaymentCount(loan.getMissedPaymentCount()+1);
                    loan.setNextPaymentDate(loan.getNextPaymentDate().plusDays(3));
                }
                else if(period.getDays()==0 && loan.getMissedPaymentCount()<4){
                    loan.setNextPaymentDate(loan.getNextPaymentDate().plusDays(3));
                    loan.setMissedPaymentCount(loan.getMissedPaymentCount()+1);
                }
                else if(loan.getMissedPaymentCount()>3){
                    Repayment repayment = new Repayment();
                    repayment.setPayAmount(Double.valueOf(loan.getMonthlyInterestAmount()));
                    repayment.setLoan(loan);
                    repayment.setMonthNumber(month);
                    repayment.setPaymentStatus(PaymentStatus.UNPAID);
                    repayment.setPenalty(Boolean.TRUE);
                    loan.getRepayments().add(repayment);
                    loan.setNextPaymentDate(loan.getStartDate().plusMonths(month+1));
                    loan.setMissedPaymentCount(0);
                    Double pay = monthlyPayAmount(loan.getRemainingAmount(),totalNoOfMonthsLeft-month);
                    loan.setRemainingAmount(loan.getRemainingAmount() + loan.getMonthlyInterestAmount() - pay);
                    loan.setMonthlyInterestAmount((int) Math.round(monthlyPayAmount(loan.getRemainingAmount(),totalNoOfMonthsLeft - month - 1)));
                    loan.setTotalMissedPayments(loan.getTotalMissedPayments()+1);
                }

            }
            if(loan.getRemainingAmount().equals(Double.valueOf(0D))) closeLoan(loan);
            loanRepository.save(loan);
        }
    }



    private ResponseEntity sendEmail(Loan loan){
        String to = loan.getAccount().getUser().getEmail();
        String subject = "Repayment of Loan";
        String body = "As your balance is lesser than the emi "+loan.getMonthlyInterestAmount()+" please add money to your account within 3 days";
        try {
            emailService.sendEmail(to, subject, body);
        }
        catch(MailException e){ResponseEntity.badRequest();}
        return new ResponseEntity(HttpStatusCode.valueOf(200));
    }



}
