package com.tc.training.smallFinance.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;

    private String lastName;

    private String password;

    private LocalDate dob;

    private String email;

    private Integer age;

    private String aadharCardNumber;

    private String panCardNumber;

    private Integer phoneNumber;

}
