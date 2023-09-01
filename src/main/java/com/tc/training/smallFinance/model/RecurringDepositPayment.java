package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class RecurringDepositPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "rId")
    private RecurringDeposit recurringDeposit;

    private Integer monthNumber;

    private Double payAmount;

    private PaymentStatus paymentStatus = PaymentStatus.UPCOMING;

    private UUID transactionId;


}
