package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Repayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "loanId")
    private Loan loan;

    private Integer monthNumber;

    private Double payAmount;

    private PaymentStatus paymentStatus = PaymentStatus.UPCOMING;

    private UUID transactionId;

    private Boolean penalty = false;


}
