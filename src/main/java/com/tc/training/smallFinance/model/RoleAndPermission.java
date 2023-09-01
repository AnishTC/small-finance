package com.tc.training.smallFinance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleAndPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID roleId;

    private RequestMethod method;

    private String uri;

    private String roles;

}
