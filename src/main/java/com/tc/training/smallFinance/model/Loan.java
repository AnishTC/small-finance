package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.Status;
import com.tc.training.smallFinance.utils.TypeOfLoans;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID loanId;

    private LocalDate appliedDate;

    private Boolean isActive = Boolean.TRUE;

    private Status status = Status.UNDER_REVIEW;

    private Double loanedAmount;

    @ManyToOne
    @JoinColumn(referencedColumnName = "slabId")
    private  Slabs slab;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails account;

    private TypeOfLoans typeOfLoan ;

    private LocalDate loanEndDate;

    private String interest;

    private Double interestAmount;

    private Double totalAmount;

    private Double remainingAmount;

    private Integer monthlyInterestAmount;

    private String penaltyInterest = "0";

    @OneToMany(mappedBy = "loan",cascade = CascadeType.ALL)
    private List<Repayment> repayments;

}
