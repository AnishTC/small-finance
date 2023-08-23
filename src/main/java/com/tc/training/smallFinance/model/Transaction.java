package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.TransactionType;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;
@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionID;

    private Long amount;

    @Enumerated
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails from;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails to;
//
//    @ManyToOne
//    @JoinColumn(referencedColumnName = "accountNumber")
//    private AccountDetails accountNumber;

    private Timestamp timestamp;

    @Enumerated
    private TypeOfTransaction  whichTransaction;

    private String description = "The "+amount+" has been "+transactionType+" for "+whichTransaction;

}
