package com.tc.training.smallFinance.dtos.outputs;

import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.UUID;
@Data
public class SlabOutputDto {

    private UUID slabId;

    private String tenures;

    private String interestRate;

    private String typeOfTransaction;
}
