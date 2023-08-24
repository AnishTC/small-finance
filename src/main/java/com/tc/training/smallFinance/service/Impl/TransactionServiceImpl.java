package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.TransactionInputDto;
import com.tc.training.smallFinance.dtos.outputs.TransactionOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.Transaction;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.TransactionRepository;
import com.tc.training.smallFinance.service.TransactionService;
import com.tc.training.smallFinance.utils.TransactionType;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;


    @Override
    public TransactionOutputDto deposit(TransactionInputDto transactionInputDto,Long accountNumber) {
        Transaction transaction = modelMapper.map(transactionInputDto, Transaction.class);
        transaction.setTo(accountRepository.findById(Long.valueOf(transactionInputDto.getTo())).orElseThrow(()->new AccountNotFoundException("To account not found")));
        transaction.setTimestamp(LocalDateTime.now());
        if (transactionInputDto.getPurpose() != null) transaction.setDescription(transactionInputDto.getPurpose());
        AccountDetails accountDetails = accountRepository.findById(accountNumber).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(transactionInputDto.getType().equals("DEPOSIT")) {
            transaction.setFrom(transaction.getTo());
            transaction.setTransactionType(TransactionType.CREDITED);
            transaction.setWhichTransaction(TypeOfTransaction.DEPOSIT);
            accountDetails.setBalance(accountDetails.getBalance() + transactionInputDto.getAmount());
        }
        else if(transactionInputDto.getType().equals("WITHDRAW")){
            transaction.setFrom(transaction.getTo());
            transaction.setTransactionType(TransactionType.DEBITED);
            transaction.setWhichTransaction(TypeOfTransaction.WITHDRAWAL);
            accountDetails.setBalance(accountDetails.getBalance() - transactionInputDto.getAmount());
        }

        else if(transactionInputDto.getType().equals("TRANSFER")){
            transaction.setFrom(accountDetails);
            transaction.setTransactionType(TransactionType.DEBITED);
            transaction.setWhichTransaction(TypeOfTransaction.TRANSFER);
            accountDetails.setBalance(accountDetails.getBalance() - transactionInputDto.getAmount());
            AccountDetails accountDetails1 = transaction.getTo();
            accountDetails1.setBalance(accountDetails1.getBalance() + transactionInputDto.getAmount());
            accountRepository.save(accountDetails1);
        }
        transactionRepository.save(transaction);
        accountRepository.save(accountDetails);
        TransactionOutputDto transactionOutputDto = modelMapper.map(transaction,TransactionOutputDto.class);
        transactionOutputDto.setAmount(transaction.getAmount());
        transactionOutputDto.setFromAccountNumber(transaction.getFrom().getAccountNumber());
        transactionOutputDto.setToAccountNumber(transaction.getTo().getAccountNumber());
        return transactionOutputDto;
    }

    @Override
    public List<TransactionOutputDto> getAllTransactions(LocalDate date1, LocalDate date2, Long accNo) {

        LocalDateTime localDateTime1;
        LocalDateTime localDateTime2;
        if (date1!=null) localDateTime1= date1.atStartOfDay();
        else localDateTime1 = accountRepository.findById(accNo).orElseThrow(()->new AccountNotFoundException("wrong account number")).getOpeningDate().atStartOfDay();

        if (date2!=null) localDateTime2= LocalDateTime.of(date2, LocalTime.of(23,59,59));
        else localDateTime2 = LocalDateTime.now();

        List<TransactionOutputDto> list  =  transactionRepository.findAllByUserAndDate(localDateTime1,localDateTime2,accNo).stream().map(transaction1->modelMapper.map(transaction1,TransactionOutputDto.class)).collect(Collectors.toList());
        Collections.sort(list, Collections.reverseOrder(Comparator.comparing(TransactionOutputDto::getTimestamp)));
        return list;

    }
}
