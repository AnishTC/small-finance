package com.tc.training.smallFinance.dtos.inputs;

import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.Slabs;
import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FdInputDto {

    private Long accountNumber;

    private String tenures;

    private Long amount;
}
