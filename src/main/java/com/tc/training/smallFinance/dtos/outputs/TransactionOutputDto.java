package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.utils.TransactionType;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionOutputDto {

    private UUID transactionID;

    private Double amount;

    private TransactionType transactionType;

    private String fromAccountNumber;

    private String toAccountNumber;

    private LocalDateTime timestamp;

    private TypeOfTransaction whichTransaction;

    private Double balance;

    private String description = "The "+amount+" has been "+transactionType+" for "+whichTransaction;


}
