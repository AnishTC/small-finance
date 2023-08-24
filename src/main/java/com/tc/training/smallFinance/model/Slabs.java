package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.Tenures;
import com.tc.training.smallFinance.utils.TypeOfTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Slabs {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID slabId;

    @Enumerated
    private Tenures tenures;

    private String IntrestRate;

    @Enumerated
    private TypeOfTransaction typeOfTransaction;
}
