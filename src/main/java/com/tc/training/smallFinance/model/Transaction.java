package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.TransactionType;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionID;

    private Double amount;

    @Enumerated
    private TransactionType transactionType;

//<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails from;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails to;

    private LocalDateTime timestamp;

    @Enumerated
    private TypeOfTransaction  whichTransaction;

    private String description = "The "+amount+" has been "+transactionType+" for "+whichTransaction;

    private Double balance;

}
