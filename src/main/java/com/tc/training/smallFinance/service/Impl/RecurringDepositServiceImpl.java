package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.RecurringDepositInputDto;
import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.RecurringDepositOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.exception.AmountNotSufficientException;
import com.tc.training.smallFinance.exception.MyMailException;
import com.tc.training.smallFinance.model.*;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.RecurringDepositRepository;
import com.tc.training.smallFinance.repository.SlabRepository;
import com.tc.training.smallFinance.service.AccountServiceDetails;
import com.tc.training.smallFinance.service.EmailService;
import com.tc.training.smallFinance.service.RecurringDepositService;
import com.tc.training.smallFinance.service.TransactionService;
import com.tc.training.smallFinance.utils.PaymentStatus;
import com.tc.training.smallFinance.utils.RdStatus;
import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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
    private AccountServiceDetails accountService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private EmailService emailService;


    @Override
    public RecurringDepositOutputDto saveRd(RecurringDepositInputDto recurringDepositInputDto) {

        RecurringDeposit rd = modelMapper.map(recurringDepositInputDto, RecurringDeposit.class);
        rd.setAccount(accountRepository.findById(recurringDepositInputDto.getAccountNumber()).orElseThrow(() -> new AccountNotFoundException("account with this id not found")));
        Slabs slab = slabRepository.findByTenuresAndTypeOfTransaction(Tenures.ONE_YEAR, TypeOfTransaction.RD);
        if(rd.getAccount().getBalance() < recurringDepositInputDto.getMonthlyPaidAmount()) throw new AmountNotSufficientException("no sufficient funds to open the account");
        rd.setInterest(slab.getInterestRate());
        rd.setStartDate(LocalDate.now());
        rd.setMaturityDate(rd.getStartDate().plusMonths(rd.getMonthTenure()));
        rd.setMaturityAmount(calculateMaturityAmount(rd,rd.getMonthTenure()));
        rd.setNextPaymentDate(rd.getStartDate().plusMonths(1));
        rd = recurringDepositRepository.save(rd);
        //monthlyPay(rd.getRId());
        RecurringDepositOutputDto rdout = modelMapper.map(rd, RecurringDepositOutputDto.class);
        rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));

        return rdout;
    }

    private Double calculateMaturityAmount(RecurringDeposit rd,Integer noOfMonths) {

        Double p = rd.getMonthlyPaidAmount() ;
        Double r = Double.valueOf(rd.getInterest()) / 100;
        Integer t =  noOfMonths / 12;
        Integer n=4;
        // Double amount = Math.pow(principal * (1 + quarterlyInterest) , (4 * years));
        Double amount = 0D;
        for(int i=noOfMonths;i>=0;i--){

            amount +=  p * Math.pow(( 1 + r/n) ,  ((i/12) * n));

        }

        return amount;
    }

    private List<RecurringDepositPayment> setPaymentSchedule(RecurringDeposit recurringDeposit) {

        List<RecurringDepositPayment> list = new ArrayList<>();
        for (int i = 0; i < recurringDeposit.getMonthTenure(); i++) {
            RecurringDepositPayment rd = new RecurringDepositPayment();
            rd.setRecurringDeposit(recurringDeposit);
            rd.setPayAmount(recurringDeposit.getMonthlyPaidAmount());
            rd.setMonthNumber(i + 1);
            list.add(rd);
        }

        return list;

    }

    @Override
    public RecurringDepositOutputDto getById(UUID id) {
        RecurringDeposit rd = recurringDepositRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("rd account not found"));
        RecurringDepositOutputDto rdout = modelMapper.map(rd, RecurringDepositOutputDto.class);
        rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));
        return rdout;
    }

    @Override
    public List<RecurringDepositOutputDto> getAll() {

        List<RecurringDeposit> rds = recurringDepositRepository.findAll();
        List<RecurringDepositOutputDto> rdouts = new ArrayList<>();
        for (RecurringDeposit r : rds) {
            RecurringDepositOutputDto rd = modelMapper.map(r, RecurringDepositOutputDto.class);
            rd.setAccount(String.valueOf(r.getAccount().getAccountNumber()));
            rdouts.add(rd);
        }
        Collections.sort(rdouts, new Comparator<RecurringDepositOutputDto>() {

            @Override
            public int compare(RecurringDepositOutputDto o1, RecurringDepositOutputDto o2) {
                if (o1.getStatus().equals(RdStatus.ACTIVE) && o2.getStatus().equals(RdStatus.ACTIVE)) return 0;
                else if (o1.getStatus().equals(RdStatus.ACTIVE) && o2.getStatus().equals(RdStatus.CLOSED)) return 1;
                else if (o1.getStatus().equals(RdStatus.ACTIVE) && o2.getStatus().equals(RdStatus.MATURED)) return 1;
                else return -1;
            }
        });
        return rdouts;
    }

    @Override
    public ResponseEntity monthlyPay(UUID id) {

        RecurringDeposit rd = recurringDepositRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("wrong id"));
        List<RecurringDepositPayment> recurringDepositPayments = rd.getRdPayments();
        Period period = Period.between(rd.getStartDate(), LocalDate.now());
        Integer monthNumber = period.getYears() * 12 + period.getMonths() + 1;
        RecurringDepositPayment repayment = null;
        for (RecurringDepositPayment r : recurringDepositPayments) {
            if (r.getMonthNumber() == monthNumber) {
                repayment = r;
                break;
            }
        }

        if (repayment == null) throw new AccountNotFoundException("RD term is over");

        //   if(repayment.getPaymentStatus().equals(PaymentStatus.PAID))  repayment.setPayAmount(repayment.getPayAmount()+rd.getMonthlyPaidAmount());

        UUID tId = setTransaction(rd, "DEBIT", rd.getMonthlyPaidAmount());
        repayment.setTransactionId(tId);
        repayment.setPaymentStatus(PaymentStatus.PAID);
        recurringDepositRepository.save(rd);
        if (monthNumber == rd.getRdPayments().size()) closeAccount(rd, "matured");

        return ResponseEntity.ok("RD monthly amount of " + rd.getMonthlyPaidAmount() + "  paid");
    }

    @Override
    public List<RecurringDepositOutputDto> getAllRecurringDeposit(Long accNo) {
        List<RecurringDeposit> rdList = recurringDepositRepository.findByAccount(accNo);
        List<RecurringDepositOutputDto> rdoutList = new ArrayList<>();
        for (RecurringDeposit rd : rdList) {
            RecurringDepositOutputDto rdout = modelMapper.map(rd, RecurringDepositOutputDto.class);
            rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));
            rdoutList.add(rdout);
        }
        return rdoutList;
    }

    @Override
    public Double getTotalMoneyInvested(Long accNo) {
        List<RecurringDeposit> rds = recurringDepositRepository.findByAccountAndStatus(accNo,RdStatus.ACTIVE.name());
        Double sum = 0D;
        for (RecurringDeposit r : rds) {
            List<RecurringDepositPayment> rpay = r.getRdPayments();
            for (RecurringDepositPayment recurringDepositPayment : rpay) {
                if (recurringDepositPayment.getPaymentStatus().equals(PaymentStatus.PAID))
                    sum += recurringDepositPayment.getPayAmount();
            }
        }
        return sum;
    }

    @Override
    public List<RecurringDepositOutputDto> getByStatus(Long accNo) {
        List<RecurringDeposit> rdList = recurringDepositRepository.findByAccountAndStatus(accNo, RdStatus.ACTIVE.name());
        List<RecurringDepositOutputDto> rdoutList = new ArrayList<>();
        for (RecurringDeposit rd : rdList) {
            RecurringDepositOutputDto rdout = modelMapper.map(rd, RecurringDepositOutputDto.class);
            rdout.setAccount(String.valueOf(rd.getAccount().getAccountNumber()));
            rdoutList.add(rdout);
        }
        return rdoutList;
    }

    private UUID setTransaction(RecurringDeposit rd, String status, Double amount) {

        TransactionInputDto transaction = new TransactionInputDto();
        if (status.equals("CREDIT")) {
            //transaction.setFrom(loan.getAccount());
            transaction.setType("RD");
            // transaction.setAmount(loan.getLoanedAmount());
            transaction.setAmount(amount);
            transaction.setAccountNumber(String.valueOf(rd.getAccount().getAccountNumber()));
            transaction.setTo(String.valueOf(rd.getAccount().getAccountNumber()));
            transaction.setPurpose("RD amount credited");
        } else {
            //transaction.setFrom(loan.getAccount());
            transaction.setType("RD");
            transaction.setAmount(amount);
            transaction.setAccountNumber(String.valueOf(rd.getAccount().getAccountNumber()));
            // transaction.setTo(String.valueOf(loan.getAccount().getAccountNumber()));
            transaction.setPurpose("RD amount debited");
        }
        transaction.setTrans(status);
        UUID tId = transactionService.deposit(transaction, rd.getAccount().getAccountNumber()).getTransactionID();
        return tId;
    }

  /*  private void scheduleCheck(){

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

}*/

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduler() {

        List<RecurringDeposit> rdList = recurringDepositRepository.findByIsActive();

        for (RecurringDeposit rd : rdList) {

            Period period = Period.between(rd.getStartDate(), LocalDate.now());
            Integer month = period.getYears() * 12 + period.getMonths();
            if(rd.getRdPayments() == null) rd.setRdPayments(new ArrayList<>());

            if (!rd.getNextPaymentDate().equals(LocalDate.now())) continue;

            if (accountService.getBalance(rd.getAccount().getAccountNumber()) > rd.getMonthlyPaidAmount()) {
                RecurringDepositPayment rdPay = new RecurringDepositPayment();
                rdPay.setRecurringDeposit(rd);
                rdPay.setTransactionId(setTransaction(rd, "DEBIT", rd.getMonthlyPaidAmount()));
                rdPay.setPaymentStatus(PaymentStatus.PAID);
                rdPay.setPayAmount(rd.getMonthlyPaidAmount());
                rdPay.setMonthNumber(month + 1);
                rd.setNextPaymentDate(rd.getStartDate().plusMonths(month + 1));
                rd.getRdPayments().add(rdPay);
            } else {
                try {
                    sendEmail(rd);
                } catch (MyMailException e) {
                }


                if (rd.getMissedPayments() < 4) {
                    rd.setNextPaymentDate(rd.getNextPaymentDate().plusDays(3));
                    rd.setMissedPayments(rd.getMissedPayments()+1);
                } else {
                    RecurringDepositPayment rdPay = new RecurringDepositPayment();
                    rdPay.setRecurringDeposit(rd);
                    rdPay.setPaymentStatus(PaymentStatus.UNPAID);
                    rdPay.setPayAmount(rd.getMonthlyPaidAmount());
                    rdPay.setMonthNumber(month + 1);
                    rd.setNextPaymentDate(rd.getStartDate().plusMonths(month + 1));
                    rd.setTotalMissedPaymentCount(rd.getTotalMissedPaymentCount() + 1);
                    rd.getRdPayments().add(rdPay);
                }

            }
            if (rd.getMaturityDate().equals(LocalDate.now())) closeAccount(rd, "matured");

            if (rd.getTotalMissedPaymentCount() > 3) {
                closeAccount(rd, "closed");
                Double total = 0D;
                for (RecurringDepositPayment rpay : rd.getRdPayments()) {
                    total += rpay.getPayAmount();
                }
                /*total = total * (1 + (Double.valueOf(rd.getInterest()) / 4) * rd.getRdPayments().size());
                total -= total * 0.2;*/
                total = calculateMaturityAmount(rd,rd.getRdPayments().size()+1);
                setTransaction(rd, "CREDIT", total);
            }

            recurringDepositRepository.save(rd);
        }


    }

    private void closeAccount(RecurringDeposit rd, String status) {

        if (status.equals("closed")) rd.setStatus(RdStatus.CLOSED);
        else rd.setStatus(RdStatus.MATURED);

        recurringDepositRepository.save(rd);
    }

    private ResponseEntity sendEmail(RecurringDeposit rd) {
        String to = rd.getAccount().getUser().getEmail();
        String subject = "Repayment of RD";
        String body = "As your balance is lesser than the emi " + rd.getMonthlyPaidAmount() + " please add money to your account within 3 days";
        try {
            emailService.sendEmail(to, subject, body);
        } catch (MailException e) {
            ResponseEntity.badRequest();
        }
        return new ResponseEntity(HttpStatusCode.valueOf(200));
    }
}
