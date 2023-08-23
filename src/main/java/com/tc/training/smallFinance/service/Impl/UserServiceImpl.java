package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.repository.UserRepository;
import com.tc.training.smallFinance.service.UserService;
import jakarta.persistence.Lob;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";


    public User addUser(AccountDetailsInputDto accountDetailsInputDto){
        User user = new User();
        user.setFirstName(accountDetailsInputDto.getFirstName());
        user.setLastName(accountDetailsInputDto.getLastName());
        user.setDob(accountDetailsInputDto.getDob());
        user.setEmail(accountDetailsInputDto.getEmail());
        user.setPanCardNumber(accountDetailsInputDto.getPanCardNumber());
        user.setAadharCardNumber(accountDetailsInputDto.getAadharCardNumber());
        user.setPhoneNumber(accountDetailsInputDto.getPhoneNumber());
        user.setAge(calculateAge(accountDetailsInputDto.getDob()));
        user.setPassword(generateRandomPassword());
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(String userName, String password) {
        User user = userRepository.findByEmail(userName);
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    public void uploadImage(MultipartFile file1, MultipartFile file2, MultipartFile file3, String userName) {
        User user = userRepository.findByEmail(userName);
        user.setAadharPhoto(convertImage(file1));
        user.setPanPhoto(convertImage(file2));
        user.setUserPhoto(convertImage(file3));
        userRepository.save(user);
    }

    @Override
    public byte[] getImage(String userName) {
        String base = userRepository.findByEmail(userName).getAadharPhoto();
        byte[] b = Base64.decodeBase64(base);
        return b;
    }

    public String convertImage(MultipartFile file) {

        String b64="";

        byte[] b;
        try {
            b = file.getBytes();
            b64 = Base64.encodeBase64String(b);
        }
        catch(IOException e){}

        return b64;
    }




    public static int calculateAge(LocalDate dob) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dob, currentDate);
        return period.getYears();
    }

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < 9; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}
