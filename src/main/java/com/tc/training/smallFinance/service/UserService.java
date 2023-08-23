package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    public User addUser(AccountDetailsInputDto accountDetailsInputDto);

    void updatePassword(String userName, String password);

    void uploadImage(MultipartFile file1, MultipartFile file2, MultipartFile file3, String userName);

    byte[] getImage(String userName);

    //void uploadImage(MultipartFile file);
}
