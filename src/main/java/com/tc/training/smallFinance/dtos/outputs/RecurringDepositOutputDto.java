package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.utils.RdStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class RecurringDepositOutputDto
{

    private UUID rId;

    private String account;

    private Integer monthTenure;

    private Double monthlyPaidAmount;

    private  Double maturityAmount;

    private LocalDate startDate;

    private LocalDate maturityDate;

    private List<Long> transactionIds;

    private RdStatus status ;

    private String interest;



}
