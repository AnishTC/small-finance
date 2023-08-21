package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.repository.UserRepository;
import com.tc.training.smallFinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Period;

public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public void addUser(AccountDetailsInputDto accountDetailsInputDto){
        User user = new User();
        user.setFirstName(accountDetailsInputDto.getFirstName());
        user.setLastName(accountDetailsInputDto.getLastName());
        user.setDob(accountDetailsInputDto.getDob());
        user.setEmail(accountDetailsInputDto.getEmail());
        user.setPanCardNumber(accountDetailsInputDto.getPanCardNumber());
        user.setAadharCardNumber(accountDetailsInputDto.getAadharCardNumber());
        user.setPhoneNumber(accountDetailsInputDto.getPhoneNumber());
        user.setAge(calculateAge(accountDetailsInputDto.getDob()));
        userRepository.save(user);
    }
   /* public User getUser(AccountDetailsOutputDto accountDetailsOutputDto){
       // return userRepository.ge
    }*/

    public static int calculateAge(LocalDate dob) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        return period.getYears();
    }
}
