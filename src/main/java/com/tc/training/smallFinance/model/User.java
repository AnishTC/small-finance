package com.tc.training.smallFinance.model;

import com.tc.training.smallFinance.utils.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    private String firstName;

    private String lastName;

    private String password;

    private LocalDate dob;

    private String email;

    private Integer age;

    private String aadharCardNumber;

    private String panCardNumber;

    private String phoneNumber;

    @Lob
    @Column( columnDefinition = "LONGBLOB")
    private String aadharPhoto;

    @Lob
    @Column( columnDefinition = "LONGBLOB")
    private String panPhoto;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String  userPhoto;

    private Role roleName = Role.CUSTOMER;

<<<<<<< HEAD
    @Lob
    @Column( columnDefinition = "BLOB")
    private String salarySlip;

    @Lob
    @Column( columnDefinition = "BLOB")
    private String homeSlip;
=======
    private String firebaseId;
>>>>>>> 813167e127b57ac0a3b1e2e1f51798fe7fabd7ab

}
