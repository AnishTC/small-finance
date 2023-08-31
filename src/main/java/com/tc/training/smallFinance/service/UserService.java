package com.tc.training.smallFinance.service;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.inputs.LoginInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoginOutputDto;
import com.tc.training.smallFinance.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {

    public User addUser(AccountDetailsInputDto accountDetailsInputDto);

    void uploadImage(MultipartFile file1, MultipartFile file2, MultipartFile file3, String userName);

    byte[] getImage(String userName);

    LoginOutputDto login(LoginInputDto loginInputDto);

    User getByFirebaseId(String userUid);
    public ResponseEntity<String> resetPassword(Long email);

    //void uploadImage(MultipartFile file);
}
