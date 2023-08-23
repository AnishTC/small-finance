package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.Tenures;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixedDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID fdId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "accountNumber")
    private AccountDetails accountNumber;

    @OneToOne
    @JoinColumn(referencedColumnName = "slabId")
    private Tenures tenures;

    private Long amount;

    private LocalDate depositedDate;

    private Boolean isActive = Boolean.TRUE;

    private LocalDate preMatureWithDrawl = null;

}
