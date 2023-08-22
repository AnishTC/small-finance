package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    public User addUser(AccountDetailsInputDto accountDetailsInputDto);

    void updatePassword(String userName, String password);

    void uploadImage(MultipartFile file, String userName);

    //void uploadImage(MultipartFile file);
}
