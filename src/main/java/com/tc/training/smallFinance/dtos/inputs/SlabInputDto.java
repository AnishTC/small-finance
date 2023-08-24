package com.tc.training.smallFinance.dtos.inputs;

import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class SlabInputDto {

    private String tenures;

    private String interestRate;

    private String typeOfTransaction;

}
