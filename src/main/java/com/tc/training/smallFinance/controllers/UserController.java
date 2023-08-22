package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PutMapping("/updatePassword")
    public ResponseEntity updatePassword(@RequestParam String userName, @RequestParam  String password){
        userService.updatePassword(userName,password);
        return ResponseEntity.ok("password successfully changed");
    }

    @PutMapping("/uploadImage")
    public ResponseEntity uploadImage(@RequestParam MultipartFile file, @RequestParam String userName) throws IOException {
        userService.uploadImage(file,userName);
        return ResponseEntity.ok("password successfully changed");
    }

}
