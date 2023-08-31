package com.tc.training.smallFinance.model;


import com.tc.training.smallFinance.utils.RdStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class RecurringDeposit {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID rId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails account;

    private Integer monthTenure;

    private Double monthlyPaidAmount;

    private  Double maturityAmount;

    private LocalDate startDate;

    private LocalDate maturityDate;

    private List<Long> transactionIds;

    @Enumerated(EnumType.STRING)
    private RdStatus status = RdStatus.ACTIVE;

    private String interest;

    @OneToMany(mappedBy = "recurringDeposit", cascade = CascadeType.ALL)
    private  List<RecurringDepositPayment> rdPayments;


}
