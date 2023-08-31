package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.outputs.FDDetails;

import java.util.List;

public interface DepositService {
    FDDetails getDetails(Long accNo);

    List<Object> getAccounts(Long accNo);
}
