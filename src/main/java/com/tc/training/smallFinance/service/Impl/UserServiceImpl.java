package com.tc.training.smallFinance.service.Impl;

import com.tc.training.smallFinance.dtos.inputs.AccountDetailsInputDto;
import com.tc.training.smallFinance.dtos.inputs.LoginInputDto;
import com.tc.training.smallFinance.dtos.outputs.AccountDetailsOutputDto;
import com.tc.training.smallFinance.dtos.outputs.LoginOutputDto;
import com.tc.training.smallFinance.dtos.outputs.TransactionOutputDto;
import com.tc.training.smallFinance.exception.AccountNotFoundException;
import com.tc.training.smallFinance.exception.UserNotFound;
import com.tc.training.smallFinance.model.AccountDetails;
import com.tc.training.smallFinance.model.Transaction;
import com.tc.training.smallFinance.model.User;
import com.tc.training.smallFinance.repository.AccountRepository;
import com.tc.training.smallFinance.repository.TransactionRepository;
import com.tc.training.smallFinance.repository.UserRepository;
import com.tc.training.smallFinance.service.TransactionService;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;



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

    @Override
    public LoginOutputDto login(LoginInputDto loginInputDto) {
            Long userName = Long.valueOf(loginInputDto.getUserName());
            User user = accountRepository.findById(userName).get().getUser();
          //  if (user==null) throw new AccountNotFoundException("Wrong account number");
            if(user==null){ throw new UserNotFound("Incorrect user name"); }
            String pass = user.getPassword();
            if(!pass.equals(loginInputDto.getPassword())) throw new UserNotFound("Incorrect password");
             AccountDetails accountDetails = accountRepository.findByUser(user);
            LoginOutputDto loginOutputDto = new LoginOutputDto();
            loginOutputDto.setFirstName(user.getFirstName());
            loginOutputDto.setLastName(user.getLastName());
            loginOutputDto.setPhoneNumber(user.getPhoneNumber());
            loginOutputDto.setBalance(accountDetails.getBalance());
            loginOutputDto.setAccNo(accountDetails.getAccountNumber());
            loginOutputDto.setKyc(accountDetails.getKyc());
            loginOutputDto.setRoleName(accountDetails.getUser().getRoleName());
           // List<TransactionOutputDto> list = transactionService.getAllTransactions(null,null, accountDetails.getAccountNumber());
           // loginOutputDto.setTransactions(list);
            return loginOutputDto;
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
